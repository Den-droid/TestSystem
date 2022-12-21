function validateTopicSearch() {
    let errorDiv = document.getElementById("error");

    let name = document.getElementsByName("name")[0];
    if (name.value.length < 3) {
        errorDiv.textContent = "Enter 3 or more characters for search!!!";
        return false;
    }

    return true;
}

function validateQuestionSearch() {
    let errorDiv = document.getElementById("error");

    let text = document.getElementsByName("text")[0];
    if (text.value.length < 3) {
        errorDiv.textContent = "Enter 3 or more characters for search!!!";
        return false;
    }

    return true;
}