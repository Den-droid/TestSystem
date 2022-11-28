function validateDeleteTopic() {
    let errorDiv = document.getElementById("error")

    let radio = document.getElementsByClassName("topicRadio");
    if (radio !== null) {
        for (let i = 0; i < radio.length; i++) {
            if (radio.item(i).checked) {
                return true;
            }
        }
        errorDiv.textContent = "Check option!!!";
        return false
    } else {
        errorDiv.textContent = "Search for topic!!!"
        return false;
    }
}