
$(window).on("load", function () {

    let $paymentType = $('#paymentType');

    let paymentItem = {
        id: 0,
        name: '',
        deleted: false,
        paymentType: 'INCOME'
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
                toastr.success("Vse ok");
            },
            error: function () {
                toastr.error('Error');
            }
        })
    })
})