
$(window).on("load", function () {

    const windowPath = window.location.pathname;
    const itemId = Number(windowPath.substring(windowPath.lastIndexOf('/') + 1, windowPath.length));
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
            case 'EXPOSE':
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
            url: '../edit-item/' + itemId,
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