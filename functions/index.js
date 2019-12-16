  
'use strict';

const functions = require('firebase-functions');
const nodemailer = require('nodemailer');
<<<<<<< HEAD
// Configure the email transport using the default SMTP transport and a GMail account.
// For Gmail, enable these:
// 1. https://www.google.com/settings/security/lesssecureapps
// 2. https://accounts.google.com/DisplayUnlockCaptcha
// For other types of transports such as Sendgrid see https://nodemailer.com/transports/
// TODO: Configure the `gmail.email` and `gmail.password` Google Cloud environment variables.
=======
const gcs = require('@google-cloud/storage');
const spawn = require('child-process-promise').spawn
 
admin.initializeApp();
const db = admin.firestore();
 
>>>>>>> 2e1df54f763a10699018d68c53ac9d78a7d9ee0a
const gmailEmail = functions.config().gmail.email;
const gmailPassword = functions.config().gmail.password;
const mailTransport = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: gmailEmail,
    pass: gmailPassword,
  },
});
 
const APP_NAME = 'FietsKoerier-AP ';
 
exports.sendWelcomeEmail = functions.auth.user().onCreate((user) => {
 
  const email = user.email;  
  const displayName = user.displayName;  
  return sendWelcomeEmail(email, displayName);
});
 
exports.sendByeEmail = functions.auth.user().onDelete((user) => {
   const email = user.email;
  const displayName = user.displayName;

  return sendGoodbyeEmail(email, displayName);
});
<<<<<<< HEAD
// [END sendByeEmail]

=======
 
>>>>>>> 2e1df54f763a10699018d68c53ac9d78a7d9ee0a
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
  

<<<<<<< HEAD
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

/*
exports.myFunctionName = functions.firestore
    .document('packages/{{packagID}}').onWrite((change, context) => {
      // ... Your code here
    });*/
=======
// Listen for changes in all documents in the 'packages' collection
exports.useWildcard = functions.firestore
    .document('packages/{packageID}')
    .onUpdate((change,context) => {
      
        db.collection('packages').doc(context.params.packageID).get()
       .then(async (doc)=> {
          
        const jsonObj = doc.data();
         
        const IS_PICKED = jsonObj["Is Picked"];
        const IS_DELIVERED = jsonObj["Is Delivered"];
        const OWNER_EMAIL = jsonObj["Owner Email"]
        const DESTINATION_EMAIL = jsonObj["Destination Email"] ;
        const customer = "dear customer";
        const DELIVERY_QR_URL =jsonObj["Delivery QR code download URL"];

        if(IS_PICKED===true&&IS_DELIVERED===false){
          console.log(1);
           await sendQrCode(DESTINATION_EMAIL,customer,DELIVERY_QR_URL);
           return null;
        }else if(IS_DELIVERED===true&&IS_PICKED===true){
          console.log(2);
          await PackageReceived(OWNER_EMAIL,customer);
          return null;
        }else
        return null; 
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
    
       mailOptions.subject = `${APP_NAME}! - Your Package is on the way !`;
      mailOptions.text = `Hey ${displayName || ''}! This email is sent
      to you by${APP_NAME}.
      <br>
      our messenger is on the way,
      please use the conformation QR code`;
      mailOptions.html = `<img src="${QrLink}">
      <br>
      <a href="${QrLink}">click this link if you can't see the QR code</a>`;
      await mailTransport.sendMail(mailOptions);
      console.log('Delivery QR is sent to ', email);
      //return 1;
    }


    async function PackageReceived(email, displayName) {
      const mailOptions = {
        from: `${APP_NAME} <noreply@fietskoerier.ap.be>`,
        to: email,
      };
     
      mailOptions.subject = `${APP_NAME}! - Your Package is delivered`;
      mailOptions.text = `Hey ${displayName || ''}! 
      This email is sent to you by ${APP_NAME}.

      Your package has reached its destination
      Thank you for using our service`;
      await mailTransport.sendMail(mailOptions);
      console.log('Delivery confirmation is sent to: ',email)
       //return 1;
    }
    
    
>>>>>>> 2e1df54f763a10699018d68c53ac9d78a7d9ee0a
