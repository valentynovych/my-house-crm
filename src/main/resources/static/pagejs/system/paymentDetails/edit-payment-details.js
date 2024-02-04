$(window).on('load', function () {

    var paymentDetails = {
        id: 2,
        companyName: '',
        companyDetails: ''
    };
    blockCardDody();
    $.ajax({
        type: 'get',
        url: 'payment-details/get-details',
        dataType: 'json',
        success: function (response) {
            paymentDetails = response;
            fillInputs(paymentDetails);
        }, error: function () {
            toastr.error("Upsss..");
        }
    });

    let $companyName = $("#companyName");
    let $companyDetails = $("#companyDetails");
    autosize($companyDetails);

    function fillInputs(item) {
        $companyName.val(item.companyName);
        $companyDetails.val(item.companyDetails)
    }

    $('input, select, textarea').on("focus", function () {
        $(this).removeClass("is-invalid");
    })

    $companyName.on("change", function () {
        paymentDetails.companyName = this.value;
    })


    $companyDetails.on("change", function () {
        paymentDetails.companyDetails = this.value;
    })

    $(".button-save").on("click", function () {
        clearAllErrorMessage();
        blockCardDody();
        let formData = new FormData();

        for (var key in paymentDetails) {
            formData.append(key, paymentDetails[key] ? paymentDetails[key] : '');
        }

        $.ajax({
            type: 'post',
            url: 'payment-details/update-details',
            processData: false,
            contentType: false,
            data: formData,
            success: function (response) {
                toastr.success(successMessage);
            },
            error: function (error) {
                console.log(error)
                printErrorMessageToField(error);
                toastr.error(errorMessage);
            }
        })
    })
})