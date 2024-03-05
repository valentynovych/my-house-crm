const topCheckbox = document.querySelector("#checked-all-top");
const bottomCheckbox = document.querySelector("#checked-all-bottom");

topCheckbox.addEventListener('change', function () {
    bottomCheckbox.checked = this.checked;
})
bottomCheckbox.addEventListener('change', function () {
    topCheckbox.checked = this.checked;
})
$(document).ready(function () {
    blockCardDody();

    $.ajax({
        url: 'messages/get-messages',
        type: 'get',
        success: function (response) {

        },
        error: function (error) {
            console.log(error);
        }
    })
})