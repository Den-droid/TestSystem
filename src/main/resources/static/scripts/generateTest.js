function addTestSubmit() {
    let errorDiv = document.getElementById("error");

    let action = document.getElementById("action");
    if (action.value !== "submit") {
        if (action.value === "searchTopics") {
            let topicPart = document.getElementsByName("topicPart")[0];
            if (topicPart.value.length === 0) {
                errorDiv.textContent = "Enter search phrase for searching topics!!!";
            } else return true;
        } else if (action.value === "searchUsers") {
            let userPart = document.getElementsByName("usernamePart")[0];
            if (userPart.value.length === 0) {
                errorDiv.textContent = "Enter search phrase for searching users!!!";
            } else return true;
        }
    } else {
        // to write

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
        for (let i = 0; i < newTopics.length; i++) {
            if (newUsers.item(i).checked)
                usersCount++;
        }

        if (usersCount === 0) {
            errorDiv.textContent = "Choose at least one user!!!";
            return false;
        }
    }
}

function addHiddenAction(e) {
    let button = e.target;
    let id = button.id;

    let form = document.getElementsByName("generateTest")[0];

    let hidden = document.createElement("input");
    hidden.setAttribute("type", "hidden");
    hidden.setAttribute("name", "action");
    hidden.setAttribute("id", "action");
    hidden.setAttribute("value", id);

    form.append(hidden);
}

let searchTopicsButton = document.getElementById("searchTopics");
searchTopicsButton.addEventListener("click", addHiddenAction);
let searchUsersButton = document.getElementById("searchUsers");
searchUsersButton.addEventListener("click", addHiddenAction);
let submitButton = document.getElementById("submit");
submitButton.addEventListener("click", addHiddenAction);