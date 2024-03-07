$(window).on("load", function () {

    let $paymentType = $('#paymentType');

    let paymentItem = {
        id: 0,
        name: '',
        deleted: false,
        paymentType: ''
    };

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
        return list;
    }

    var $name = $("#name");
    $paymentType.select2({
        placeholder: 'Дохід/Витрати',
        minimumResultsForSearch: -1,
        dropdownParent: $(".card-body"),
        ajax: {
            type: "GET",
            url: 'get-item-types',
            processResults: function (response) {
                const mapResponse = new Map(Object.entries(response));
                return {
                    results: parseToMap(mapResponse)
                };
            }
        }
    });

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
            url: 'add-item',
            processData: false,
            contentType: false,
            data: formData,
            success: function (response) {
                toastr.success("Редагування успішно завершено");
                window.history.back();
            },
            error: function (error) {
                console.log(error)
                printErrorMessageToField(error);
                toastr.error("Помилка час редагування сталась помилка :(");
            }
        })
    })
})