function validateUserProfile() {
    let errorDiv = document.getElementById("error");

    let username = document.getElementsByName("username")[0];
    if (username.value.length < 6) {
        errorDiv.textContent = "You can't have username with length less than 6!!!";
        return false;
    }

    let firstName = document.getElementsByName("firstName")[0];
    if (firstName.value.length === 0) {
        errorDiv.textContent = "You can't leave firstname empty!!!";
        return false;
    }

    let lastName = document.getElementsByName("lastName")[0];
    if (lastName.value.length === 0) {
        errorDiv.textContent = "You can't leave lastname empty!!!";
        return false;
    }

    let email = document.getElementsByName("email")[0];
    if (email.value.length === 0) {
        errorDiv.textContent = "You can't leave email empty!!!";
        return false;
    }

    let changePasswordCheckbox = document.getElementsByName("changePassword")[0];
    let checked = changePasswordCheckbox.checked;
    if (checked) {
        let password = document.getElementsByName("password")[0];
        let confirmPassword = document.getElementsByName("confirmPassword")[0];
        if (password.value.length < 8 || confirmPassword.value.length < 8) {
            errorDiv.textContent = "Your passwords must be at least 8 characters length!!!";
            return false;
        }
        if (password.value !== confirmPassword.value) {
            errorDiv.textContent = "Your passwords must match!!!";
            return false;
        }
    }

    return true;
}

function addHiddenAction(e) {
    let button = e.target;
    let id = button.id;

    let form = document.getElementsByName("editProfile")[0];

    let existingHidden = document.getElementById("action");
    if (existingHidden !== null) {
        existingHidden.setAttribute("value", id);
    } else {
        let hidden = document.createElement("input");
        hidden.setAttribute("type", "hidden");
        hidden.setAttribute("name", "action");
        hidden.setAttribute("id", "action");
        hidden.setAttribute("value", id);

        form.append(hidden);
    }
}

function changePassword(e) {
    let checked = e.target.checked;
    let password = document.getElementsByName("password")[0];
    let confirmPassword = document.getElementsByName("confirmPassword")[0];
    if (checked) {
        password.disabled = false;
        confirmPassword.disabled = false;
    } else {
        password.disabled = true;
        confirmPassword.disabled = true;
    }
}

let deleteButton = document.getElementById("delete");
let editButton = document.getElementById("edit");
let changePasswordCheckbox = document.getElementsByName("changePassword")[0];
editButton.addEventListener("click", addHiddenAction);
deleteButton.addEventListener("click", addHiddenAction);
changePasswordCheckbox.addEventListener("click", changePassword);
