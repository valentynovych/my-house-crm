$(window).on("load", function () {


    $('.show-password').on('click', function () {
        var find = $(this).parent().find("input.password");
        var type = find.attr('type');
        if (type === 'password') {
            find.attr('type', 'text');
            $(this).html('<i class="ti ti-eye"></i>');
        } else if (type === 'text') {
            find.attr('type', 'password');
            $(this).html('<i class="ti ti-eye-off"></i>');
        }
    })

    $('.generate-password').on('click', function () {
        let password = generatePassword();
        $('.password').val(password).trigger('change');
    })

    $('.button-cancel').on('click', function () {
        window.history.back();
    })

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

    var $role = $("#roleId");

    function getRoleLabel(role) {
        switch (role) {
            case 'DIRECTOR':
                return roleDirector;
            case 'MANAGER':
                return roleManager;
            case 'ACCOUNTANT':
                return roleAccountant;
            case 'ELECTRICIAN':
                return roleElectrician;
            case 'PLUMBER':
                return rolePlumber;
        }
    }

    $role.select2({
        placeholder: roleLabel,
        minimumResultsForSearch: -1,
        dropdownParent: $(".card-body"),
        ajax: {
            type: "GET",
            url: 'get-roles',
            processResults: function (response) {
                return {
                    results: $.map(response, function (role) {
                        return {
                            id: role.id,
                            text: getRoleLabel(role.name)
                        }
                    })
                };
            }
        }
    });

    function generatePassword() {
        var chars = "0123456789abcdefghijklmnopqrstuvwxyz!@#$%^&*()ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        var passwordLength = 12;
        var password = "";

        for (var i = 0; i <= passwordLength; i++) {
            var randomNumber = Math.floor(Math.random() * chars.length);
            password += chars.substring(randomNumber, randomNumber + 1);
        }
        return password;
    }

    let staff = {
        firstName: '',
        lastName: '',
        phoneNumber: '',
        email: '',
        password: '',
        confirmPassword: '',
        roleId: ''
    };

    $('input, select').on('change', function () {
        staff[$(this).attr('id')] = this.value;
    })

    $(".button-save").on("click", function () {
        clearAllErrorMessage();
        blockCardDody();
        trimInputsValue();

        let formData = new FormData();

        for (var key in staff) {
            formData.append(key, staff[key]);
        }

        $.ajax({
            type: 'post',
            url: window.location.href,
            processData: false,
            contentType: false,
            data: formData,
            success: function (response) {
                window.history.back();
            },
            error: function (error) {
                console.log(error);
                printErrorMessageToField(error);
                toastr.error(errorAddMessage);
            }
        })
    })
})