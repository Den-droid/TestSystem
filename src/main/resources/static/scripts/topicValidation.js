function topicValidation() {
    let errorDiv = document.getElementById("error");

    let topicName = document.getElementById("name");
    if (topicName.value.length < 6) {
        errorDiv.textContent = "Topic name must contain at least 3 characters!!!";
        return false;
    }

    return true;
}