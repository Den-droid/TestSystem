function timeOver() {
    let answerType = document.getElementsByName("answerType")[0].value;
    let answers = document.getElementsByName("answers");

    if (answerType === 'Match') {
        for (let i = 0; i < answers.length; i++) {
            for (let j = 0; j < answers[i].options.length; j++) {
                if (answers[i].options.item(j).value === answers[i].value) {
                    continue;
                }
                answers[i].options.item(j).disabled = true;
            }
        }
    } else if (answerType === 'Single' || answerType === 'Multiple') {
        for (let i = 0; i < answers.length; i++) {
            if (answers[i].checked) {
                continue;
            }
            answers[i].disabled = true;
        }
    } else {
        for (let i = 0; i < answers.length; i++) {
            answers.item(i).readonly = true;
        }
    }
}

function addHiddenAction(e) {
    let button = e.target;
    let id = button.id;

    let form = document.getElementsByName("testWalkthrough")[0];

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

function timer() {
    let timer = document.getElementById("timeLeft").innerHTML;
    if (timer === '00:00:00' || timer === '00:00') {
        document.getElementById("timeLeft").innerHTML = "EXPIRED";
        timeOver();
        return;
    }

    let splitTimer = timer.split(":");
    let millis = +splitTimer[0] * (60 * 60 * 1000) + +splitTimer[1] * (60 * 1000) + +splitTimer[2] * 1000;
    let countDownDate = new Date().getTime() + millis;

    let x = setInterval(function () {

        let now = new Date().getTime();

        let distance = countDownDate - now;

        let hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        let minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        let seconds = Math.floor((distance % (1000 * 60)) / 1000);


        let string = hours < 10 ? "0" + hours : hours;
        string += ":";
        string += minutes < 10 ? "0" + minutes : minutes;
        string += ":";
        string += seconds < 10 ? "0" + seconds : seconds;
        document.getElementById("timeLeft").innerHTML = string;

        if (distance < 0) {
            clearInterval(x);
            document.getElementById("timeLeft").innerHTML = "EXPIRED";
            timeOver();
        }
    }, 1000);
}

let previousButton = document.getElementById("previous");
if (previousButton !== null)
    previousButton.addEventListener("click", addHiddenAction);

let nextButton = document.getElementById("next");
if (nextButton !== null)
    nextButton.addEventListener("click", addHiddenAction);

let submitButton = document.getElementById("submit");
if (submitButton !== null)
    submitButton.addEventListener("click", addHiddenAction);

if (document.getElementById("timeLeft") != null)
    timer();