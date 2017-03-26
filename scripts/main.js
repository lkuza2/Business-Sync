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

// Initializes BusinessSync.
function BusinessSync() {
  this.checkSetup();

  // Shortcuts to DOM Elements.
  this.contactList = document.getElementById('contacts');
  // this.messageInput = document.getElementById('message');
  // this.submitButton = document.getElementById('submit');
  // this.submitImageButton = document.getElementById('submitImage');
  // this.imageForm = document.getElementById('image-form');
  // this.mediaCapture = document.getElementById('mediaCapture');
  this.userPic = document.getElementById('user-pic');
  this.userName = document.getElementById('user-name');
  this.signInButton = document.getElementById('sign-in');
  this.signOutButton = document.getElementById('sign-out');
  this.signInSnackbar = document.getElementById('must-signin-snackbar');

  // Saves message on form submit.
  this.signOutButton.addEventListener('click', this.signOut.bind(this));
  this.signInButton.addEventListener('click', this.signIn.bind(this));

  // Toggle for the button.
  // var buttonTogglingHandler = this.toggleButton.bind(this);
  // this.messageInput.addEventListener('keyup', buttonTogglingHandler);
  // this.messageInput.addEventListener('change', buttonTogglingHandler);

  // Events for image upload.
  // this.submitImageButton.addEventListener('click', function(e) {
  //   e.preventDefault();
  //   this.mediaCapture.click();
  // }.bind(this));
  // this.mediaCapture.addEventListener('change', this.saveImageMessage.bind(this));

  this.initFirebase();
}

// Sets up shortcuts to Firebase features and initiate firebase auth.
BusinessSync.prototype.initFirebase = function() {
  // Shortcuts to Firebase SDK features.
  this.auth = firebase.auth();
  this.database = firebase.database();
  this.storage = firebase.storage();
  // Initiates Firebase auth and listen to auth state changes.
  this.auth.onAuthStateChanged(this.onAuthStateChanged.bind(this));
};

// Loads chat messages history and listens for upcoming ones.
BusinessSync.prototype.loadContacts = function() {
  // Reference to the /messages/ database path.
  this.contactsRef = this.database.ref('Users');
  // Make sure we remove all previous listeners.
  this.contactsRef.off();

  // Loads the last 12 messages and listen for new ones.
  var setMessage = function(data) {
      var val = data.val();
      this.displayContact(data.key, val.firstName, val.lastName, val.email, val.wrkemail, val.company, val.phone);
  }.bind(this);
  this.contactsRef.on('child_added', setMessage);
  this.contactsRef.on('child_changed', setMessage);
};

// Saves a new message on the Firebase DB.
// BusinessSync.prototype.saveMessage = function(e) {
//   e.preventDefault();
//   // Check that the user entered a message and is signed in.
//   if (this.messageInput.value && this.checkSignedInWithMessage()) {
//     var currentUser = this.auth.currentUser;
//     // Add a new message entry to the Firebase Database.
//     this.messagesRef.push({
//         name: currentUser.displayName,
//         text: this.messageInput.value,
//         photoUrl: currentUser.photoURL || '/images/profile_placeholder.png'
//     }).then(function() {
//         // Clear message text field and SEND button state.
//         BusinessSync.resetMaterialTextfield(this.messageInput);
//         this.toggleButton();
//     }.bind(this)).catch(function(error) {
//         console.error('Error writing new message to Firebase Database', error);
//     });
//   }
// };

// Sets the URL of the given img element with the URL of the image stored in Cloud Storage.
// BusinessSync.prototype.setImageUrl = function(imageUri, imgElement) {
//   // If the image is a Cloud Storage URI we fetch the URL.
//   if (imageUri.startsWith('gs://')) {
//       imgElement.src = BusinessSync.LOADING_IMAGE_URL; // Display a loading image first.
//       this.storage.refFromURL(imageUri).getMetadata().then(function(metadata) {
//           imgElement.src = metadata.downloadURLs[0];
//       });
//   } else {
//       imgElement.src = imageUri;
//   }
// };

// Saves a new message containing an image URI in Firebase.
// This first saves the image in Firebase storage.
// BusinessSync.prototype.saveImageMessage = function(event) {
//   event.preventDefault();
//   var file = event.target.files[0];
//
//   // Clear the selection in the file picker input.
//   this.imageForm.reset();
//
//   // Check if the file is an image.
//   if (!file.type.match('image.*')) {
//     var data = {
//       message: 'You can only share images',
//       timeout: 2000
//     };
//     this.signInSnackbar.MaterialSnackbar.showSnackbar(data);
//     return;
//   }
//   // Check if the user is signed-in
//   if (this.checkSignedInWithMessage()) {
//     // We add a message with a loading icon that will get updated with the shared image.
//     var currentUser = this.auth.currentUser;
//     this.messagesRef.push({
//         name: currentUser.displayName,
//         imageUrl: BusinessSync.LOADING_IMAGE_URL,
//         photoUrl: currentUser.photoURL || '/images/profile_placeholder.png'
//     }).then(function(data) {
//
//         // Upload the image to Cloud Storage.
//         var filePath = currentUser.uid + '/' + data.key + '/' + file.name;
//         return this.storage.ref(filePath).put(file).then(function(snapshot) {
//
//             // Get the file's Storage URI and update the chat message placeholder.
//             var fullPath = snapshot.metadata.fullPath;
//             return data.update({imageUrl: this.storage.ref(fullPath).toString()});
//         }.bind(this));
//     }.bind(this)).catch(function(error) {
//         console.error('There was an error uploading a file to Cloud Storage:', error);
//     });
//   }
// };

// Signs-in Friendly Chat.
BusinessSync.prototype.signIn = function() {
  // Sign in Firebase using popup auth and Google as the identity provider.
  var provider = new firebase.auth.GoogleAuthProvider();
  this.auth.signInWithPopup(provider);
};

// Signs-out of Friendly Chat.
BusinessSync.prototype.signOut = function() {
  // Sign out of Firebase.
  this.auth.signOut();
};

// Triggers when the auth state change for instance when the user signs-in or signs-out.
BusinessSync.prototype.onAuthStateChanged = function(user) {
  if (user) { // User is signed in!
    // Get profile pic and user's name from the Firebase user object.
    var profilePicUrl = user.photoURL; // Only change these two lines!
    var userName = user.displayName;   // Only change these two lines!

    // Set the user's profile pic and name.
    this.userPic.style.backgroundImage = 'url(' + profilePicUrl + ')';
    this.userName.textContent = userName;

    // Show user's profile and sign-out button.
    this.userName.removeAttribute('hidden');
    this.userPic.removeAttribute('hidden');
    this.signOutButton.removeAttribute('hidden');

    // Hide sign-in button.
    this.signInButton.setAttribute('hidden', 'true');

    // We load currently existing chant messages.
    this.loadContacts();

    // We save the Firebase Messaging Device token and enable notifications.
    this.saveMessagingDeviceToken();
  } else { // User is signed out!
    // Hide user's profile and sign-out button.
    this.userName.setAttribute('hidden', 'true');
    this.userPic.setAttribute('hidden', 'true');
    this.signOutButton.setAttribute('hidden', 'true');

    // Show sign-in button.
    this.signInButton.removeAttribute('hidden');
  }
};

// Returns true if user is signed-in. Otherwise false and displays a message.
BusinessSync.prototype.checkSignedInWithMessage = function() {
  // Return true if the user is signed in Firebase
  if (this.auth.currentUser) {
      return true;
  }

  // Display a message to the user using a Toast.
  var data = {
    message: 'You must sign-in first',
    timeout: 2000
  };
  this.signInSnackbar.MaterialSnackbar.showSnackbar(data);
  return false;
};

// Saves the messaging device token to the datastore.
BusinessSync.prototype.saveMessagingDeviceToken = function() {
  // TODO(DEVELOPER): Save the device token in the realtime datastore
};

// Requests permissions to show notifications.
BusinessSync.prototype.requestNotificationsPermissions = function() {
  // TODO(DEVELOPER): Request permissions to send notifications.
};

// Resets the given MaterialTextField.
BusinessSync.resetMaterialTextfield = function(element) {
  element.value = '';
  element.parentNode.MaterialTextfield.boundUpdateClassesHandler();
};

// Template for contacts.
BusinessSync.CONTACT_TEMPLATE =
    '<div class="contact-container">' +
    '<div class="firstName"></div>' +
    '<div class="lastName"></div>' +
    '<div class="workEmail"></div>' +
    '<div class="company"></div>' +
    '</div>';

// A loading image URL.
BusinessSync.LOADING_IMAGE_URL = 'https://www.google.com/images/spin-32.gif';

// Displays a Message in the UI.
BusinessSync.prototype.displayContact = function(key, firstName, lastName, email, workEmail, company, phone) {
  var div = document.getElementById(key);
  // If an element for that message does not exists yet we create it.
  if (!div) {
    var container = document.createElement('div');
    container.innerHTML = '<div class="contact-container"><h3 class="name"></h3><div class="phone"></div><a class="workEmail"></a><div class="company"></div></div>';
    // container.innerHTML = BusinessSync.CONTACT_TEMPLATE;
    div = container.firstChild;
    div.setAttribute('id', key);
    this.contactList.appendChild(div);
  }
  div.querySelector('.name').textContent = firstName + ' ' + lastName;
  div.querySelector('.phone').textContent = phone;
  div.querySelector('.workEmail').textContent = workEmail;
  var mailtoLink = 'mailto:' + workEmail;
  div.querySelector('.workEmail').setAttribute('href', mailtoLink);
  div.querySelector('.company').textContent = company;

  // Show the card fading-in.
  setTimeout(function() {div.classList.add('visible')}, 1);
  this.contactList.scrollTop = this.contactList.scrollHeight;
};

// Enables or disables the submit button depending on the values of the input
// fields.
// BusinessSync.prototype.toggleButton = function() {
//   if (this.messageInput.value) {
//     this.submitButton.removeAttribute('disabled');
//   } else {
//     this.submitButton.setAttribute('disabled', 'true');
//   }
// };

// Checks that the Firebase SDK has been correctly setup and configured.
BusinessSync.prototype.checkSetup = function() {
  if (!window.firebase || !(firebase.app instanceof Function) || !window.config) {
    window.alert('You have not configured and imported the Firebase SDK. ' +
        'Make sure you go through the codelab setup instructions.');
  } else if (config.storageBucket === '') {
    window.alert('Your Cloud Storage bucket has not been enabled. Sorry about that. This is ' +
        'actually a Firebase bug that occurs rarely. ' +
        'Please go and re-generate the Firebase initialisation snippet (step 4 of the codelab) ' +
        'and make sure the storageBucket attribute is not empty. ' +
        'You may also need to visit the Storage tab and paste the name of your bucket which is ' +
        'displayed there.');
  }
};

window.onload = function() {
  window.BusinessSync = new BusinessSync();
};
