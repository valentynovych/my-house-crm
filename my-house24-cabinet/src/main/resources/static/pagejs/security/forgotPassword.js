function send(){
    clearAllErrorMessage();
    $.ajax({
        type : "POST",
        url : "forgotPassword",
        data : {
            email: $('#email').val()
        },
        success : function() {
            window.location.href = 'sentToken';
        },
        error : function(error) {
            printErrorMessageToField(error);
        }
    });
}