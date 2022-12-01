function validateRegister() {
    let errorDiv = document.getElementById("error");

    let username = document.getElementById("username");
    let password = document.getElementById("password");
    let confirmPassword = document.getElementById("confirmPassword");
    let firstName = document.getElementById("firstName");
    let lastName = document.getElementById("lastName");
    let email = document.getElementById("email");

    if (username.value.length < 6 || username.value.indexOf(' ') >= 0) {
        errorDiv.textContent = "Enter correct username (must be equal or greater than 6 characters " +
            "and not contain white spaces)!!!";
        return false;
    } else if (password.value.length < 8) {
        errorDiv.textContent = "Enter correct password (must be equal or greater than 8 characters)!!!";
        return false;
    } else if (confirmPassword.value.length === 0) {
        errorDiv.textContent = "Confirm password!!!";
        return false;
    } else if (confirmPassword.value !== password.value) {
        errorDiv.textContent = "Passwords do not match!!!";
        return false;
    } else if (firstName.value.length === 0) {
        errorDiv.textContent = "Enter first name!!!";
        return false;
    } else if (lastName.value.length === 0) {
        errorDiv.textContent = "Enter last name!!!";
        return false;
    } else if (email.value.length === 0) {
        errorDiv.textContent = "Enter email!!!";
        return false;
    }

    return true;
}