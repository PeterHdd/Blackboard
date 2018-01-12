const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);




exports.pushnotifi =
functions.database.ref('/messages/{pushId}').onWrite( event => { //arrow function instead of onWrite( function(event){..}

console.log('Push notification event triggered');
var valueObject = event.data.val(); //event.data is the data return, val() returns type of data



 var titlesname=valueObject.title;
 var bodysname=valueObject.message;
 var teachername=valueObject.teachname;



//Create a notification
const payload = {
    data: {
   titles: titlesname,
            bodys: bodysname,
            teachname: teachername,
            sound: "default"
    }
  };

const options = {
    priority: "high",
    timeToLive: 60 * 60 * 24
};

return admin.messaging().sendToTopic(valueObject.title, payload, options);

});

exports.assignmentnotification=functions.database.ref('/Assignment/{pushid}').onCreate( event=> {

console.log('triggered');

var valueObject=event.data.val();
console.log("it is "+ valueObject.coursename);
console.log("title is "+ valueObject.body);

const payload={
         data:{
           title: "New Assignment",
           body: "You have a new Assignment",
           coursename: valueObject.coursename,
           sound:"default"
           },
       };
const options = {
    priority: "high",
    timeToLive: 60 * 60 * 24
};
return admin.messaging().sendToTopic(valueObject.coursename, payload, options);
});
