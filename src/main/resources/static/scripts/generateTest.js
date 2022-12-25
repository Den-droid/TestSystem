function addTestSubmit() {
    let errorDiv = document.getElementById("error");
    errorDiv.style.display = "block";

    let action = document.getElementById("action");
    if (action.value !== "submit") {
        if (action.value === "searchTopics") {
            let topicPart = document.getElementsByName("topicPart")[0];
            if (topicPart.value.length < 3) {
                errorDiv.textContent = "Enter at least 3 characters to start search!!!";
                return false;
            } else {
                errorDiv.style.display = "block";
                return true;
            }
        } else if (action.value === "searchUsers") {
            let userPart = document.getElementsByName("usernamePart")[0];
            if (userPart.value.length < 3) {
                errorDiv.textContent = "Enter at least 3 characters to start search!!!";
                return false;
            } else {
                errorDiv.style.display = "block";
                return true;
            }
        }
    } else {
        let name = document.getElementById("name");
        if (name.value.length < 3) {
            errorDiv.textContent = "Enter at least 3 characters for test name!!!";
            return false;
        }

        let startDateString = document.getElementById("startDate").value;
        let finishDateString = document.getElementById("finishDate").value;

        if (new Date(finishDateString).getTime() - new Date(startDateString).getTime() < 60 * 1000) {
            errorDiv.textContent = "Start date must be before finish date on 1 minute or more!!!";
            return false;
        }

        let questionsCount = document.getElementById("questionCount");
        if (questionsCount.value.length === 0) {
            errorDiv.textContent = "You must have at least 1 question!!!";
            return false;
        }

        let topicsCount = document.getElementsByClassName("oldTopics").length;
        let newTopics = document.getElementsByClassName("newTopics");
        for (let i = 0; i < newTopics.length; i++) {
            if (newTopics.item(i).checked)
                topicsCount++;
        }

        if (topicsCount === 0) {
            errorDiv.textContent = "Choose at least one topic!!!";
            return false;
        }

        let usersCount = document.getElementsByClassName("oldUsers").length;
        let newUsers = document.getElementsByClassName("newUsers");
        for (let i = 0; i < newUsers.length; i++) {
            if (newUsers.item(i).checked)
                usersCount++;
        }

        if (usersCount === 0) {
            errorDiv.textContent = "Choose at least one user (except yourself)!!!";
            return false;
        }

        errorDiv.style.display = "block";
        return true;
    }
}

function addHiddenAction(e) {
    let button = e.target;
    let id = button.id;

    let form = document.getElementsByName("generateTest")[0];

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

let searchTopicsButton = document.getElementById("searchTopics");
searchTopicsButton.addEventListener("click", addHiddenAction);
let searchUsersButton = document.getElementById("searchUsers");
searchUsersButton.addEventListener("click", addHiddenAction);
let submitButton = document.getElementById("submit");
submitButton.addEventListener("click", addHiddenAction);