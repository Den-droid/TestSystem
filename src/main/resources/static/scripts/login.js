function validateLogin() {
    let errorDiv = document.getElementById("error");

    let username = document.getElementById("username");
    let password = document.getElementById("password");
    if (username.value.length < 6) {
        errorDiv.textContent = "Enter correct username (must be equal or greater than 6 characters)!!!";
        return false;
    } else if (password.value.length < 8) {
        errorDiv.textContent = "Enter correct password (must be equal or greater than 8 characters)!!!";
        return false
    }

    return true
}