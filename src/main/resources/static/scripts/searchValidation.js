function validateSearch() {
    let errorDiv = document.getElementById("error");
    errorDiv.style.display = "block";

    let name = document.getElementsByName("query")[0];
    if (name.value.length < 3) {
        errorDiv.textContent = "Enter 3 or more characters for search!!!";
        return false;
    }

    errorDiv.style.display = "none";
    return true;
}

function validateTestSearch() {
    let errorDiv = document.getElementById("error");
    errorDiv.style.display = "block";

    let name = document.getElementsByName("query")[0];
    if (name.value.length > 0 && name.value.length < 3) {
        errorDiv.textContent = "Enter 3 or more characters for search!!!";
        return false;
    }

    errorDiv.style.display = "none";
    return true;
}