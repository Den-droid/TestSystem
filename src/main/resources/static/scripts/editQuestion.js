function validateEditForm() {
    if (!validateQuestionText())
        return false;

    if (!validateMediaEditQuestion())
        return false;

    if (!validateAnswerDescription())
        return false;

    if (!validateAnswers())
        return false;

    return true;
}

function validateMediaEditQuestion() {
    let errorDiv = document.getElementById("error");
    errorDiv.style.display = "block";

    let newQuestionType = document.getElementById("selectQuestionType").value;
    let initialQuestionType = document.cookie
        .split("; ")
        .find((row) => row.startsWith("initialQuestionType"))
        ?.split("=")[1];
    let media = document.getElementById("media");

    if (newQuestionType !== initialQuestionType && newQuestionType !== 'Text') {
        if (media.value === '') {
            errorDiv.textContent = "Insert media!!!";
            return false;
        }
    }

    errorDiv.style.display = "none";
    return true;
}

let answerDivs = document.getElementsByClassName("answerDiv");
let addAnswerButton = document.getElementById("addAnswerButton");
for (let i = 0; i < answerDivs.length; i++) {
    answerDivs.item(i).lastElementChild.addEventListener("click", deleteAnswer);
}
if (answerTypeSelect.value === "Single" || answerTypeSelect.value === "Multiple") {
    addAnswerButton.addEventListener("click", addSingleAnswer);
} else if (answerTypeSelect.value === "Match") {
    addAnswerButton.addEventListener("click", addMatchAnswer);
} else if (answerTypeSelect.value === "Custom") {
    addAnswerButton.addEventListener("click", addCustomAnswer);
}
if (answerTypeSelect.value === "Single") {
    let checkboxes = document.getElementsByClassName("isCorrect");
    for (let i = 0; i < checkboxes.length; i++) {
        checkboxes.item(i).addEventListener("click", validateSingleCorrectAnswer);
    }
}

let initialQuestionType = document.getElementById("selectQuestionType").value;
document.cookie = "initialQuestionType=" + initialQuestionType + "; ";