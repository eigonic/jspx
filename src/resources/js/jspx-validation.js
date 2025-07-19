function validateFormData(group, groupName, submitterId) {

	var types = group[1];
	var element;
	var error;
	var correct = true;
	var temp = true;
	var ok;
	for ( var i = 0; i < group[0].length; i++) {
		element = document.getElementById(group[0][i]);
		if (element == null) {
			var elements = document.getElementsByName(group[0][i]);
			if (elements == null || elements.length == 0)
				continue;
			else
				element = elements[0];
		}

		error = document.getElementById(group[0][i] + 'Error' + types[i]);
		if (types[i] == 1) {
			ok = required(element);
		} else if (types[i] == 2) {
			ok = validate_email(element);
		} else if (types[i].charAt(0) == '3') {
			var splitted = types[i].split(",");
			var min = splitted[1];
			var max = splitted[2];
			ok = valLength(element, min, max);
		} else if (types[i].charAt(0) == '4') {
			var splitted = types[i].split(",");
			var min = splitted[1];
			var max = splitted[2];
			ok = validateRange(element, min, max);
		} else if (types[i].charAt(0) == '5') {
			ok = validateMSISDN(element);
		} else if (types[i].charAt(0) == '6') {
			var splitted = types[i].split(",");
			var digits = splitted[1];
			var mindigits = splitted[2];
			var maxdigits = splitted[3];
			ok = validateNumeric(element, digits, mindigits, maxdigits);
		} else if (types[i].charAt(0) == '7') {
			ok = true;
		}

		correct = correct && ok;
		jspx_showError(element, error, ok, types[i]);
	}
	return correct;
}
function jspx_showError(element, error, isValid, highlightedBy) {
	try {

		if (!isValid) {
			error.style.display = 'inline';
			focusElement(element);
			// if no one had highlighted this before then I can store the old
			// style
			if (!element.hlb || element.hlb == '') {
				// Set who had highlighted this element as error.
				element.hlb = highlightedBy;
				element.oldBack = element.style.backgroundColor;
				element.oldBorder = element.style.borderColor;
			}
			element.style.backgroundColor = '#FFBABA';
			element.style.borderColor = '#D8000C';
		} else {
			error.style.display = 'none';
			if (element.hlb == highlightedBy) {
				// this was highlighted by me before, so I will clear it.
				element.style.backgroundColor = element.oldBack;
				element.style.borderColor = element.oldBorder;
			}
		}
	} catch (e) {
	}
}
/**
 * focus on the element only if it has no event listeners to the focus event
 * 
 */
function focusElement(element) {
	try {
		var focusEvent = $jspx._data($jspx("#" + element.id)[0], "events").focus;
		if (focusEvent)
			return;
	} catch (e) {
		element.focus();

	}
}
function required(field) {
	if (field.type == "radio" || field.type == "checkbox") {
		var group = document.getElementsByName(field.name);
		var found = false;
		for ( var i = 0; i < group.length; ++i) {
			if (group[i].checked != null && group[i].checked) {
				found = true;
				break;
			}
		}
		return found;
	} else {
		with (field) {
			if (value == null || value == '')
				return false;
			return true;
		}
	}
}
function validate_email(field) {
	with (field) {
		return value == null
				|| value == ""
				|| checkRegexp(
						value,
						/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i);
	}
}
function valLength(field, min, max) {
	if (field) {
		var minVal=parseInt(min);
		var maxVal=parseInt(max);
		return field.value.length >= minVal && field.value.length <= maxVal;
	}
}
function validateRange(field, min, max) {
	if (field) {
		if (field.value == null || field.value == "")
			return true;
		var val = parseInt(field.value);
		if (isNaN(val))
			return false;
		else
			return val >= min && val <= max;
	}
}
function validateMSISDN(field) {
	with (field) {
		if (value == null || value == "")
			return true;
		if (value.length < 10)
			return false;
		return validateNumeric1(field);

	}
}
function validateNumeric1(field) {
	with (field) {
		if (value == null || value == "")
			return true;
		for ( var i = 0; i < value.length; i++) {
			var val = parseInt(value.charAt(i));
			if (isNaN(val))
				return false;
		}
		return true;
	}
}

function validateNumeric(field, digits, mindigits, maxdigits) {
	if (field) {
		if (field.value == null || field.value == "")
			return true;
		for ( var i = 0; i < value.length; i++) {
			if (field.value.charAt(i) == '-' || field.value.charAt(i) == '.')
				continue;
			var val = parseInt(field.value.charAt(i));
			if (isNaN(val))
				return false;
		}
		if (digits != null && digits != "") {
			if (field.value.length != digits)
				return false;
		}
		if (mindigits != null && mindigits != "") {
			if (field.value.length < mindigits)
				return false;
		}
		if (maxdigits != null && maxdigits != "") {
			if (field.value.length > maxdigits)
				return false;
		}
		return true;
	}
}

function showJspxMessage(icon) {
	var panel = document.getElementById(icon.id + 'Msg');
	panel.style.position = 'absolute';
	panel.style.display = 'inline';
	panel.style.zIndex = 10;
}
function hideJspxMessage(icon) {
	var panel = document.getElementById(icon.id + 'Msg');
	panel.style.display = 'none';
	panel.style.zIndex = -10;
}

function checkRegexp(val, regexp) {
	return regexp.test(val);
}