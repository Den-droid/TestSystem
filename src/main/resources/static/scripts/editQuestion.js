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

    let newQuestionType = document.getElementById("selectQuestionType").value;
    let initialQuestionType = document.cookie
        .split("; ")
        .find((row) => row.startsWith("initialQuestionType"))
        ?.split("=")[1];
    let media = document.getElementById("media");

    console.log(initialQuestionType)
    console.log(newQuestionType)
    if (newQuestionType !== initialQuestionType && newQuestionType !== 'Text') {
        if (media.value === '') {
            errorDiv.textContent = "Insert media!!!";
            return false;
        }
    }

    return true;
}

if (answerTypeSelect.value === "Single" || answerTypeSelect.value === "Multiple"
    || answerTypeSelect.value === "Match") {
    let answerDivs = document.getElementsByClassName("answerDiv");
    let addAnswerButton = document.getElementById("addAnswerButton");
    for (let i = 0; i < answerDivs.length; i++) {
        answerDivs.item(i).lastElementChild.addEventListener("click", deleteAnswer);
    }
    if (answerTypeSelect.value === "Single" || answerTypeSelect.value === "Multiple") {
        addAnswerButton.addEventListener("click", addSingleAnswer);
    } else {
        addAnswerButton.addEventListener("click", addMatchAnswer);
    }
    if (answerTypeSelect.value === "Single") {
        let checkboxes = document.getElementsByClassName("isCorrect");
        for (let i = 0; i < checkboxes.length; i++) {
            checkboxes.item(i).addEventListener("click", validateSingleCorrectAnswer);
        }
    }
}

let initialQuestionType = document.getElementById("selectQuestionType").value;
document.cookie = "initialQuestionType=" + initialQuestionType + "; ";