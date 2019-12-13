 /*
// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
const nodemailer = require('nodemailer');

//to make it work you need gmail account
const gmailEmail = functions.config().gmail.login;
const gmailPassword = functions.config().gmail.pass;
const customerAccount = functions.config().mailOptions.user;
admin.initializeApp();

//creating function for sending emails
var goMail = function (message) {

//transporter is a way to send your emails
    const transporter = nodemailer.createTransport({
        service: 'gmail',
        auth: {
            user: gmailEmail,
            pass: gmailPassword
        }
    });

    // setup email data with unicode symbols
    //this is how your email are going to look like
    const mailOptions = {
        from: gmailEmail, // sender address
        to: 's109802@ap.be', // list of receivers
        subject: 'Hello ✔', // Subject line
        text: '!' + message, // plain text body
        html: '!' + message // html body
    };

    //this is callback function to return status to firebase console
    const getDeliveryStatus = function (error, info) {
        if (error) {
            return console.log(error);
        }
        console.log('Message sent: %s', info.messageId);
        // Message sent: <b658f8ca-6296-ccf4-8306-87d57a0b4321@example.com>
    };

    //call of this function send an email, and return status
    transporter.sendMail(mailOptions, getDeliveryStatus);
};

//.onDataAdded is watches for changes in database
exports.onDataAdded = functions.database.ref('/emails/{sessionId}').onCreate(function (snap, context) {

    //here we catch a new data, added to firebase database, it stored in a snap variable
    const createdData = snap.val();
    var text = createdData.mail;

    //here we send new data using function for sending emails
    goMail(text);
});



*/
/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
const nodemailer = require('nodemailer');
const gcs = require('@google-cloud/storage');
const spawn = require('child-process-promise').spawn
//
admin.initializeApp();
const db = admin.firestore();

//refernce the databank -> packages collection 
// exports.sendQR2Receiver = functions.firestore
// .document('packages/{packageID}').onCreate(async (change,context)=>{
//   //TODO: send an email
//    const newPackage = await db.collection('packages').doc('{packageID}').get();
//   const emailRec = newPackage.get('Destination Email');
//   var  storageRef =functions.storage.object().ref();
//   var QRimagesRef = storageRef.child("/qrpackages/{packageeID}");
// return sendQR2Receiver(emailRec,QRimagesRef);
// });
// //

// ////////////const w = require(@google-cloud/storage)
// // Sends a welcome email to the given user.
// async function  sendQR2Receiver(email, Qrcode) {
//   const mailOptions = {
//     from: `${APP_NAME} <noreply@fietskoerier.ap.be>`,
//     to: email,
//   };
 

  
//    mailOptions.subject = `Here's a QR to the package is on the way ${APP_NAME}!`;
//   mailOptions.text = `Hey ${displayName || ''}! Welcome to ${APP_NAME}. I hope you will enjoy our service.
//   <img src="${Qrcode}" alt="Scan me!">

//   `; 
//   mailOptions
//   await mailTransport.sendMail(mailOptions);
//   console.log('Qr is Sent', email);
//   return null;
// }

// Configure the email transport using the default SMTP transport and a GMail account.
// For Gmail, enable these:
// 1. https://www.google.com/settings/security/lesssecureapps
// 2. https://accounts.google.com/DisplayUnlockCaptcha
// For other types of transports such as Sendgrid see https://nodemailer.com/transports/
// TODO: Configure the `gmail.email` and `gmail.password` Google Cloud environment variables.
const gmailEmail = functions.config().gmail.email;
const gmailPassword = functions.config().gmail.password;
const mailTransport = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: gmailEmail,
    pass: gmailPassword,
  },
});

// Your company name to include in the emails
// TODO: Change this to your app or company name to customize the email sent.
const APP_NAME = 'FietsKoerier-AP ';

// [START sendWelcomeEmail]
/**
 * Sends a welcome email to new user.
 */
// [START onCreateTrigger]
exports.sendWelcomeEmail = functions.auth.user().onCreate((user) => {
// [END onCreateTrigger]
  // [START eventAttributes]
  const email = user.email; // The email of the user.
  const displayName = user.displayName; // The display name of the user.
  // [END eventAttributes]

  return sendWelcomeEmail(email, displayName);
});
// [END sendWelcomeEmail]

// [START sendByeEmail]
/**
 * Send an account deleted email confirmation to users who delete their accounts.
 */
// [START onDeleteTrigger]
exports.sendByeEmail = functions.auth.user().onDelete((user) => {
// [END onDeleteTrigger]
  const email = user.email;
  const displayName = user.displayName;

  return sendGoodbyeEmail(email, displayName);
});
// [END sendByeEmail]
 

// Sends a welcome email to the given user.
async function sendWelcomeEmail(email, displayName) {
  const mailOptions = {
    from: `${APP_NAME} <noreply@fietskoerier.ap.be>`,
    to: email,
  };

  // The user subscribed to the newsletter.
  mailOptions.subject = `Welcome to ${APP_NAME}!`;
  mailOptions.text = `Hey ${displayName || ''}! Welcome to ${APP_NAME}. I hope you will enjoy our service.`;
  await mailTransport.sendMail(mailOptions);
  console.log('New welcome email sent to:', email);
  return null;
}

// Sends a goodbye email to the given user.
async function sendGoodbyeEmail(email, displayName) {
  const mailOptions = {
    from: `${APP_NAME} <noreply@firebase.com>`,
    to: email,
  };

  // The user unsubscribed to the newsletter.
  mailOptions.subject = `Bye!`;
  mailOptions.text = `Hey ${displayName || ''}!, We confirm that we have deleted your ${APP_NAME} account.`;
  await mailTransport.sendMail(mailOptions);
  console.log('Account deletion confirmation email sent to:', email);
  return null;
}

/**
 * TODO: edit the following function and deploy it in order to send an automatic email 
 * if somedata is being added to the Database 
 * forExample:.onDataAdded is watches for changes in database packages/{{something}}
 * 
 */
/*
exports.onDataAdded = functions.database.ref('/emails/{sessionId}').onCreate(function (snap, context) {

    //here we catch a new data, added to firebase database, it stored in a snap variable
    const createdData = snap.val();
    var text = createdData.mail;

    //here we send new data using function for sending emails
    goMail(text);
});

*/



// Listen for changes in all documents in the 'users' collection
exports.useWildcard = functions.firestore
    .document('packages/{packageID}')
    .onWrite((change,context) => {
      // If we set `/users/marie` to {name: "Marie"} then
      // context.params.userId == "marie"
      // ... and ...
      // change.after.data() == {name: "Marie"}
      //console.log(change.fieldsProto)
       //console.log(context.params.packagID);
        db.collection('packages').doc(context.params.packageID).get()
       .then(async (doc)=> {
         /*
         console.log(doc.id);
         console.log(context.params.packageID);
         console.log(doc.data());*/
        const jsonObj = doc.data();
        //change this line into Destination
         const emailRx = jsonObj["Destination Email"] ;
         console.log(emailRx);
        const customer = "dear customer";

         const QrRx = jsonObj["Pickup QR code download URL"]
        console.log(QrRx);
 
            console.log(doc);
            return await sendQrCode(emailRx,customer,QrRx);
       })
       .catch(err=>{
         console.log('Error getting the document', err);
         process.exit();
       })
     });
     
     async function sendQrCode(email, displayName,QrLink) {
      const mailOptions = {
        from: `${APP_NAME} <noreply@fietskoerier.ap.be>`,
        to: email,
      };
    
      // The user subscribed to the newsletter.
      mailOptions.subject = `${APP_NAME}! - Your Package is on the way !`;
      mailOptions.text = `Hey ${displayName || ''}! This email is sent
      to you by${APP_NAME}.
      <br>
      our messenger is on the way,
      please use the conformation QR code 
      <img src="${QrLink}" >`;
      await mailTransport.sendMail(mailOptions);
      console.log('New welcome email sent to:', email);
      //return 1;
    }
    
    