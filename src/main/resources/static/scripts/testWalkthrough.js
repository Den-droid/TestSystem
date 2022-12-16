function timeOver() {
    let answers = document.getElementsByName("answer");
    for (let i = 0; i < answers.length; i++) {
        answers.item(i).readonly = true;
    }
}

function timer() {
    let timer = document.getElementById("timeLeft").innerHTML;
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

timer();