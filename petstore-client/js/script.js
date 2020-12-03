//declare variables for password validations
const password = document.getElementById("password");
const confirm_password = document.getElementById("confirm_password");
const errorMessage = document.getElementById('error_password');
const errorMessageConfirm = document.getElementById('error_confirm');
const register = document.getElementById('register');

let password_value;
let confirm_password_value;

//declare variables for phone number validation

const phone = document.getElementById("phone");
const errorMessagePhone = document.getElementById('error_phone');

//declare variables for phone number validation
const email = document.getElementById("email");
const errorMessageEmail = document.getElementById('error_email');

//declare variables for zip code validation
const zip = document.getElementById("zip");
const errorMessageZip = document.getElementById('error_zip');


//Adding event listeners
try {
    password.addEventListener("keyup", passwordChecker, false);
    confirm_password.addEventListener("keyup", passwordChecker, false);
} catch (e) {}
phone.addEventListener("keyup", phone_checker, false);
email.addEventListener("keyup", email_checker, false);
zip.addEventListener("keyup", zip_checker, false);


//Function to check password
function passwordChecker() {
    password_value = password.value;

    if (password_value.length < 8) {
        errorMessage.innerText = "Password is too short";
        register.setAttribute("disabled", "disabled");
    } else {
        if (password_value.match(/\d/) === null) {
            errorMessage.innerText = "Password must contain at least one number.";
            register.setAttribute("disabled", "disabled");
        } else {
            if (password_value.match(/[#%\-@_&*!]/)) {
                errorMessage.innerText = "";
                passwordCompare();
            } else {
                errorMessage.innerText = "Password must contain at least one special character: #%-@_&*!";
                register.setAttribute("disabled", "disabled");
            }

        }
    }
}

//Function to compare passwords
function passwordCompare() {
    confirm_password_value = confirm_password.value;
    if (password_value !== confirm_password_value) {
        errorMessageConfirm.innerText = "Passwords are not the same";
        register.setAttribute("disabled", "disabled");
    } else {
        errorMessageConfirm.innerText = "";
        //document.getElementById("login").disabled = false;

        register.removeAttribute("disabled");
    }

}

//Function to check phone number

function phone_checker() {
    const phone_value = phone.value;

    if (phone_value.match(/\(?([0-9]{3})\)?([ .-]?)([0-9]{3})\2([0-9]{4})/)) {
        errorMessagePhone.innerText = "";
        register.removeAttribute("disabled");

    } else {
        errorMessagePhone.innerText = "Invalid phone number.";
        register.setAttribute("disabled", "disabled");
    }

}

function validateEmail(email) {
    const re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}

//Function to check email address
function email_checker() {
    const email_value = email.value;

    if (validateEmail(email_value)) {
        errorMessageEmail.innerText = "";
        register.removeAttribute("disabled");

    } else {
        errorMessageEmail.innerText = "Invalid Email address.";
        register.setAttribute("disabled", "disabled");
    }
}

// Function to check zip code
function zip_checker() {
    const zip_value = zip.value;
    if (zip_value.match(/(^\d{5}$)|(^\d{5}-\d{4}$)/)) {
        errorMessageZip.innerText = "";
        register.removeAttribute("disabled");
    } else {
        errorMessageZip.innerText = "Invalid Zip Code.";
        register.setAttribute("disabled", "disabled");
    }
}
