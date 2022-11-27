function topicValidation() {
    let errorDiv = document.getElementById("error");

    let topicName = document.getElementById("name");
    if (topicName.value.length === 0) {
        errorDiv.textContent = "Enter topic name!!!";
        return false;
    }

    return true;
}