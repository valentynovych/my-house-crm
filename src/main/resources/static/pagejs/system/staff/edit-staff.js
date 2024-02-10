var $role = $("#roleId");
let $status = $('#status');
let staff = {
    // id: 0,
    // firstName: '',
    // lastName: '',
    // phoneNumber: '',
    // email: '',
    // password: '',
    // confirmPassword: '',
    // roleId: '',
    // status: ''
};

let staffToRestore;

$(window).on("load", function () {


    let href = window.location.href;
    staff.id = href.slice(href.lastIndexOf('/') + 1, href.length);

    $role.select2({
        placeholder: roleLabel,
        minimumResultsForSearch: -1,
        dropdownParent: $(".card-body"),
        ajax: {
            type: "GET",
            url: '../get-roles',
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


    $status.select2({
        dropdownParent: $('.card-body'),
        placeholder: statusLabel,
        minimumResultsForSearch: -1,
        ajax: {
            type: "GET",
            url: '../get-statuses',
            dataType: 'json',
            processResults: function (response) {
                return {
                    results: $.map(response, function (status) {
                        return {
                            id: status,
                            text: getStatusLabel(status)
                        }
                    })
                };
            }
        }
    });

    $('input, select').on('change', function () {
        staff[$(this).attr('id')] = this.value;
    });

    blockCardDody();
    $.ajax({
        type: 'get',
        url: '../get-staff/' + staff.id,
        success: function (response) {
            staffToRestore = JSON.parse(JSON.stringify(response));
            staff = response;
            fillInputs(staff);
        }, error: function (error) {
            toastr.error(staffErrorMessage);
        }
    })
})

function getStatusLabel(status) {
    switch (status) {
        case 'NEW':
            return statusNew;
        case 'ACTIVE':
            return statusActive;
        case 'DISABLED':
            return statusDisabled;
        default:
            return 'Не відомий'
    }
}

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

function fillInputs(staff) {
    let $current = $('#current-staff-link');
    let attr = $current.attr('href');
    $current.attr('href', attr + staff.id);

    $('#firstName').val(staff.firstName);
    $('#lastName').val(staff.lastName);
    $('#phoneNumber').val(staff.phoneNumber);
    $('#email').val(staff.email);
    $('<option value="' + staff.role.id + '">' + getRoleLabel(staff.role.name) + '</option>').appendTo('#roleId');
    $role.trigger('change');
    $('<option value="' + staff.status + '">' + getStatusLabel(staff.status) + '</option>').appendTo('#status');
    $status.trigger('change');
}

$(".button-save").on("click", function () {
    blockCardDody();
    clearAllErrorMessage();

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
            printErrorMessageToField(error);
            toastr.error(errorMessage);
        }
    })
})

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
});

$('.generate-password').on('click', function () {
    let password = generatePassword();
    $('.password').val(password).trigger('change');
})

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

$('.button-cancel').on('click', function () {
    fillInputs(staffToRestore);
})