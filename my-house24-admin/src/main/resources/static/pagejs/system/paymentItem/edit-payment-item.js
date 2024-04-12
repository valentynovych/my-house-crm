$(window).on("load", function () {

    const itemId = window.location.pathname.match(/\d+$/);
    let itemTypes;
    let $paymentType = $('#paymentType');

    let paymentItem = {
        id: 0,
        name: '',
        deleted: false,
        paymentType: 'INCOME'
    };

    function getStatusLabel(status) {
        switch (status) {
            case 'INCOME':
                return 'Дохід';
            case 'EXPENSE':
                return 'Витрати';
            default:
                return 'Не відомо';
        }
    }

    function parseToMap(data) {
        const mapResponse = new Map(data);
        const list = [];

        for (const [key, value] of mapResponse) {
            const res = {
                id: key,
                text: value,
            };
            list.push(res);
        }
        itemTypes = list;
        return list;
    }

    blockCardDody();
    $.ajax({
        type: 'get',
        url: '../get-item/' + itemId,
        dataType: 'json',
        success: function (response) {
            paymentItem = response;
            fillInputs(paymentItem);
        }, error: function () {
            toastr.error("Upsss..");
        }
    });

    var $name = $("#name");

    function fillInputs(item) {
        $name.val(item.name);
        $paymentType.select2({
            placeholder: 'Дохід/Витрати',
            minimumResultsForSearch: -1,
            dropdownParent: $(".card-body"),
            data: [{
                id: item.paymentType,
                text: getStatusLabel(item.paymentType)
            }],
            ajax: {
                type: "GET",
                url: '../get-item-types',
                processResults: function (response) {
                    const mapResponse = new Map(Object.entries(response));
                    return {
                        results: parseToMap(mapResponse)
                    };
                }
            }
        });
    }

    $('input, select, textarea').on("focus", function () {
        $(this).removeClass("is-invalid");
    })

    $name.on("change", function () {
        paymentItem.name = this.value;
    })

    $paymentType.on("change", function () {
        paymentItem.paymentType = this.value;
    })

    $(".button-save").on("click", function () {
        clearAllErrorMessage();
        blockCardDody();

        let formData = new FormData();

        for (var key in paymentItem) {
            formData.append(key, paymentItem[key]);
        }

        $.ajax({
            type: 'post',
            url: '../edit-item/' + itemId,
            processData: false,
            contentType: false,
            data: formData,
            success: function (response) {
                toastr.success("Стаття успішно створенна");
                window.history.back();
            },
            error: function (error) {
                printErrorMessageToField(error);
                toastr.error("Сталась помилка під час створення статті :(");
            }
        })
    })
})