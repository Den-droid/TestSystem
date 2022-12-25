function topicValidation() {
    let errorDiv = document.getElementById("error");
    errorDiv.style.display = "block";

    let topicName = document.getElementById("name");
    if (topicName.value.length < 6) {
        errorDiv.textContent = "Topic name must contain at least 6 characters!!!";
        return false;
    }

    errorDiv.style.display = "none";
    return true;
}