function questionTypeChange() {
    let selectQuestionType = document.getElementById("selectQuestionType");
    let media = document.getElementById("media");
    media.disabled = selectQuestionType.value === 'Text';
}

function answerTypeChange() {
    let answers = document.getElementById("answersDiv");
    if (answers !== null) {
        while (answers.firstChild) {
            answers.removeChild(answers.lastChild)
        }
    }

    let answerTypeSelect = document.getElementById("selectAnswerType");
    let value = answerTypeSelect.value;
    switch (value) {
        case "Single":
        case "Multiple":
            addSingleAnswer()
            break
        case "Match":
            addMatchAnswer()
            break
        case "Custom":
            addCustomAnswer()
    }
}

function addSingleAnswer() {
    let answersDiv = document.getElementById("answersDiv");
    let answerDivs = answersDiv.getElementsByClassName("answerDiv");

    let answerDiv = document.createElement("div");
    answerDiv.setAttribute("class", "answerDiv")

    let hidden = document.createElement("input");
    hidden.setAttribute("type", "hidden");
    hidden.setAttribute("name", "answerIds");

    let input = document.createElement("input");
    input.setAttribute("type", "text");
    input.setAttribute("name", "answers")
    input.setAttribute("class", "answer")
    input.setAttribute("id", "answer" + answerDivs.length)

    let inputLabel = document.createElement("label");
    inputLabel.setAttribute("for", "answer" + answerDivs.length)
    inputLabel.textContent = " Enter answer: ";

    let checkbox = document.createElement("input");
    checkbox.setAttribute("type", "checkbox")
    checkbox.setAttribute("name", "isCorrect")
    checkbox.setAttribute("class", "isCorrect")
    checkbox.setAttribute("id", "checkbox" + answerDivs.length)
    checkbox.setAttribute("value", "" + answerDivs.length)

    let answerTypeSelect = document.getElementById("selectAnswerType");
    if (answerTypeSelect.value === 'Single')
        checkbox.addEventListener("click", validateSingleCorrectAnswer);

    let checkboxLabel = document.createElement("label");
    checkboxLabel.setAttribute("for", "checkbox" + answerDivs.length)
    checkboxLabel.textContent = " Set correct ";

    let deleteAnswerButton = document.createElement("button");
    deleteAnswerButton.setAttribute("type", "button");
    deleteAnswerButton.textContent = " Delete answer ";
    deleteAnswerButton.addEventListener("click", deleteAnswer);

    answerDiv.appendChild(hidden);
    answerDiv.appendChild(inputLabel);
    answerDiv.appendChild(input);
    answerDiv.appendChild(checkbox);
    answerDiv.appendChild(checkboxLabel);
    answerDiv.appendChild(deleteAnswerButton);

    if (answerDivs.length !== 0) {
        let lastAnswerDiv = answerDivs.item(answerDivs.length - 1)

        answersDiv.insertBefore(answerDiv, lastAnswerDiv.nextSibling);
    } else {
        if (document.getElementById("addAnswerButton") === null) {
            let addAnswerButton = document.createElement("button");
            addAnswerButton.setAttribute("type", "button");
            addAnswerButton.setAttribute("id", "addAnswerButton");
            addAnswerButton.textContent = " Add answer ";
            addAnswerButton.addEventListener("click", addSingleAnswer);
            answersDiv.appendChild(addAnswerButton);
        }

        answersDiv.appendChild(answerDiv);
    }
}

function addMatchAnswer() {
    let answersDiv = document.getElementById("answersDiv");
    let answerDivs = answersDiv.getElementsByClassName("answerDiv");

    let answerDiv = document.createElement("div");
    answerDiv.setAttribute("class", "answerDiv")

    let hiddenAnswerIds = document.createElement("input");
    hiddenAnswerIds.setAttribute("type", "hidden");
    hiddenAnswerIds.setAttribute("name", "answerIds");

    let hiddenSubQuestionIds = document.createElement("input");
    hiddenSubQuestionIds.setAttribute("type", "hidden");
    hiddenSubQuestionIds.setAttribute("name", "subQuestionIds");

    let subQuestion = document.createElement("input");
    subQuestion.setAttribute("type", "text");
    subQuestion.setAttribute("name", "subQuestions");
    subQuestion.setAttribute("class", "subQuestion");
    subQuestion.setAttribute("id", "subQuestion" + answerDivs.length);

    let subQuestionLabel = document.createElement("label");
    subQuestionLabel.setAttribute("for", "subQuestion" + answerDivs.length);
    subQuestionLabel.textContent = " Enter first part: ";

    let br = document.createElement("br");

    let input = document.createElement("input");
    input.setAttribute("type", "text");
    input.setAttribute("name", "answers")
    input.setAttribute("class", "answer")
    input.setAttribute("id", "answer" + answerDivs.length)

    let inputLabel = document.createElement("label");
    inputLabel.setAttribute("for", "answer" + answerDivs.length)
    inputLabel.textContent = " Enter second part: ";

    let deleteAnswerButton = document.createElement("button");
    deleteAnswerButton.setAttribute("type", "button");
    deleteAnswerButton.textContent = " Delete answer ";
    deleteAnswerButton.addEventListener("click", deleteAnswer);

    answerDiv.appendChild(hiddenSubQuestionIds)
    answerDiv.appendChild(hiddenAnswerIds)
    answerDiv.appendChild(subQuestionLabel);
    answerDiv.appendChild(subQuestion);
    answerDiv.appendChild(br);
    answerDiv.appendChild(inputLabel);
    answerDiv.appendChild(input);
    answerDiv.appendChild(deleteAnswerButton);

    if (answerDivs.length !== 0) {
        let lastAnswerDiv = answerDivs.item(answerDivs.length - 1)

        answersDiv.insertBefore(answerDiv, lastAnswerDiv.nextSibling);
    } else {
        if (document.getElementById("addAnswerButton") === null) {
            let addAnswerButton = document.createElement("button");
            addAnswerButton.setAttribute("type", "button");
            addAnswerButton.setAttribute("id", "addAnswerButton");
            addAnswerButton.textContent = "Add answer";
            addAnswerButton.addEventListener("click", addMatchAnswer);
            answersDiv.appendChild(addAnswerButton);
        }

        answersDiv.appendChild(answerDiv);
    }
}

function addCustomAnswer() {
    let answersDiv = document.getElementById("answersDiv");
    let answerDivs = answersDiv.getElementsByClassName("answerDiv");

    let answerDiv = document.createElement("div");
    answerDiv.setAttribute("class", "answerDiv");

    let hidden = document.createElement("input");
    hidden.setAttribute("type", "hidden");
    hidden.setAttribute("name", "answerIds");

    let input = document.createElement("input");
    input.setAttribute("type", "text");
    input.setAttribute("name", "answers");
    input.setAttribute("class", "answer")
    input.setAttribute("id", "answer" + answerDivs.length);

    let label = document.createElement("label");
    label.setAttribute("for", "answer" + answerDivs.length);
    label.textContent = "Enter custom answer: ";

    let deleteAnswerButton = document.createElement("button");
    deleteAnswerButton.setAttribute("type", "button");
    deleteAnswerButton.textContent = " Delete answer ";
    deleteAnswerButton.addEventListener("click", deleteAnswer);

    answerDiv.appendChild(hidden);
    answerDiv.appendChild(label);
    answerDiv.appendChild(input);
    answerDiv.appendChild(deleteAnswerButton);

    if (answerDivs.length !== 0) {
        let lastAnswerDiv = answerDivs.item(answerDivs.length - 1)

        answersDiv.insertBefore(answerDiv, lastAnswerDiv.nextSibling);
    } else {
        if (document.getElementById("addAnswerButton") === null) {
            let addAnswerButton = document.createElement("button");
            addAnswerButton.setAttribute("type", "button");
            addAnswerButton.setAttribute("id", "addAnswerButton");
            addAnswerButton.textContent = " Add answer ";
            addAnswerButton.addEventListener("click", addCustomAnswer);
            answersDiv.appendChild(addAnswerButton);
        }

        answersDiv.appendChild(answerDiv);
    }
}

function deleteAnswer(e) {
    let answerDiv = e.target.parentNode;
    let answersDiv = answerDiv.parentNode;
    answersDiv.removeChild(answerDiv);
}

function validateAddForm() {
    if (!validateQuestionText())
        return false;

    if (!validateMedia())
        return false;

    if (!validateAnswerDescription())
        return false;

    if (!validateAnswers())
        return false;

    return true;
}

function validateQuestionText() {
    let errorDiv = document.getElementById("error");

    let questionText = document.getElementById("questionText");
    if (questionText.value.trim().length === 0) {
        errorDiv.textContent = "Enter question text!!!";
        return false;
    }
    return true;
}

function validateMedia() {
    let errorDiv = document.getElementById("error");

    let media = document.getElementById("media");
    if (media.disabled === false) {
        if (media.value === '') {
            errorDiv.textContent = "Insert media!!!";
            return false;
        }
    }
    return true;
}

function validateAnswerDescription() {
    let errorDiv = document.getElementById("error");

    let answerDescription = document.getElementById("answerDescription");
    if (answerDescription.value.trim().length === 0) {
        errorDiv.textContent = "Enter answer description!!!";
        return false;
    }
    return true;
}

function validateAnswers() {
    let errorDiv = document.getElementById("error");

    let answerType = document.getElementById("selectAnswerType");
    if (answerType.value === 'Custom') {
        let answers = document.getElementsByClassName("answer");
        for (let i = 0; i < answers.length; i++) {
            if (answers.item(i).value.length === 0) {
                errorDiv.textContent = "Enter all options!!!";
                return false;
            }
            for (let j = i + 1; j < answers.length; j++) {
                if (answers.item(i).value === answers.item(j).value) {
                    errorDiv.textContent = "All variants must be different!!!";
                    return false;
                }
            }
        }
    } else if (answerType.value === 'Single' || answerType.value === 'Multiple') {
        let answers = document.getElementsByClassName("answer");
        let isCorrect = document.getElementsByClassName("isCorrect");
        let isCorrectNumber = 0;
        for (let i = 0; i < answers.length; i++) {
            if (answers.item(i).value.length === 0) {
                errorDiv.textContent = "Enter all options!!!";
                return false;
            }
            if (isCorrect.item(i).checked) {
                isCorrectNumber++;
            }
            for (let j = i + 1; j < answers.length; j++) {
                if (answers.item(i).value === answers.item(j).value) {
                    errorDiv.textContent = "All variants must be different!!!";
                    return false;
                }
            }
        }
        if (isCorrectNumber === 0) {
            errorDiv.textContent = "Set at least one option correct!!!";
            return false;
        }
    } else if (answerType.value === 'Match') {
        let answers = document.getElementsByClassName("answer");
        let subQuestions = document.getElementsByClassName("subQuestion");
        for (let i = 0; i < answers.length; i++) {
            if (answers.item(i).value.length === 0 || subQuestions.item(i).value.length === 0) {
                errorDiv.textContent = "Enter all fields!!!";
                return false;
            }
            for (let j = i + 1; j < answers.length; j++) {
                if (subQuestions.item(i).value === subQuestions.item(j).value) {
                    errorDiv.textContent = "All second parts must be different!!!";
                    return false;
                }
                if (answers.item(i).value === answers.item(j).value) {
                    errorDiv.textContent = "All first parts must be different!!!";
                    return false;
                }
            }
        }
    }

    return true;
}

function validateSingleCorrectAnswer(e) {
    if (e.target.checked === true) {
        let isCorrectElems = document.getElementsByClassName("isCorrect");
        for (let i = 0; i < isCorrectElems.length; i++) {
            if (isCorrectElems.item(i) !== e.target && isCorrectElems.item(i).checked === true) {
                e.target.checked = false;
                break;
            }
        }
    }
}

let questionTextSelect = document.getElementById("selectQuestionType");
questionTextSelect.addEventListener('change', questionTypeChange);

let answerTypeSelect = document.getElementById("selectAnswerType");
answerTypeSelect.addEventListener('change', answerTypeChange);