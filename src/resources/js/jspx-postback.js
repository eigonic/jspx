var trunOffPanelRefresh = false;

var JSPX_ACTIVE_SERVER_REQ = false;

function postBack(invokerId, eventName, submitterId, ajaxSubmitterId,
		disableAfterClick, eventArgs, eventType, groupNames) {
	if (JSPX_ACTIVE_SERVER_REQ)
		return;
	var myform = document.getElementById(submitterId);
	if (myform == null) {
		myform = document.forms[0];
		document.getElementById('_Invoker').value = invokerId;
		document.getElementById('_EventName').value = eventName;
		document.getElementById('_EventArgs').value = eventArgs;
		document.getElementById('_EventType').value = eventType;
		if (groupNames)
			document.getElementById("_Group").value = groupNames;
		else
			document.getElementById("_Group").value = '';
		document.getElementById('_EventTarget').value = ajaxSubmitterId;
	} else {
		myform.elements[0].value = invokerId;
		myform.elements[1].value = eventName;
		myform.elements[2].value = eventArgs;
		myform.elements[3].value = eventType;
		if (groupNames)
			myform.elements[4].value = groupNames;
		else
			myform.elements[4].value = '';

		myform.elements[5].value = ajaxSubmitterId;

	}
	var invokerElement = document.getElementById(invokerId);
	if (invokerElement != null) {
		if ($jspx(invokerElement).attr('data-loading-text')) {
			$jspx(invokerElement).button('loading');
		}
		var loadImg = $jspx(invokerElement).attr('data-loading-img');
		if (loadImg) {
			$jspx(invokerElement).css('pointer-events', 'none');
			if (loadImg == 'true')
				$jspx('.jspxloadingImage').clone().appendTo(
						$jspx(invokerElement)).show();
			else
				$jspx(loadImg).clone().appendTo($jspx(invokerElement)).show();
		}
		if ($jspx(invokerElement).attr('data-loading-text')) {
			$jspx(invokerElement).button('loading');
		}
		if (disableAfterClick)
			invokerElement.disabled = 'disabled';
		else
			invokerElement.removeAttribute("disabled");
	}
	if (ajaxSubmitterId == null || ajaxSubmitterId == ''
			|| ajaxSubmitterId == 'null') {
		if (myform.onsubmit && myform.onsubmit != '') {
			myform.onsubmit();
		}
		JSPX_ACTIVE_SERVER_REQ = true;
		myform.submit();
	} else
		submitAjax(myform, invokerId, ajaxSubmitterId);
}

function validatePostBack(invokerId, eventName, submitterId, ajaxSubmitterId,
		groupNames, disableAfterClick, eventArgs) {
	var validInput = validateListOfGroups(groupNames, submitterId);
	var invoker = document.getElementById(invokerId);
	if (validInput == true) {
		postBack(invokerId, eventName, submitterId, ajaxSubmitterId,
				disableAfterClick, eventArgs, "", groupNames);
		return;
	} else if (disableAfterClick) {
		if (invoker)
			invoker.removeAttribute("disabled");
	}
	if (invoker.tagName != 'A' && invoker.tagName != 'a')
		return false;
}

function postInternal(invokerId, eventName, eventArgs, submitterId,
		ajaxSubmitterId) {
	post_ternal(invokerId, eventName, eventArgs, submitterId, ajaxSubmitterId,
			'internal');
}

function postExternal(invokerId, eventName, eventArgs, submitterId,
		ajaxSubmitterId) {
	post_ternal(invokerId, eventName, eventArgs, submitterId, ajaxSubmitterId,
			'external');
}
function postInvokeMethod(invokerId, eventName, eventArgs, submitterId,
		ajaxSubmitterId) {
	post_ternal(invokerId, eventName, eventArgs, submitterId, ajaxSubmitterId,
			'invokeMethod');
}
function post_ternal(invokerId, eventName, eventArgs, submitterId,
		ajaxSubmitterId, eventType) {
	var myform = document.getElementById(submitterId);
	if (myform == null) {
		document.getElementById('_EventArgs').value = eventArgs;
		document.getElementById('_EventType').value = eventType;
		document.getElementById('_Group').value = '';
	} else {
		myform.elements[2].value = eventArgs;
		myform.elements[3].value = eventType;
		myform.elements[4].value = '';
	}
	postBack(invokerId, eventName, submitterId, ajaxSubmitterId, false,
			eventArgs, eventType);
}

function refreshPanel(time, eventName, submitterId, ajaxSubmitterId, eventArgs,
		groupNames) {
	// check if the refreshFlag is turned Off
	if (trunOffPanelRefresh) {
		setTimeout('refreshPanel(' + time + ',"' + eventName + '","'
				+ submitterId + '","' + ajaxSubmitterId + '","' + eventArgs
				+ '","' + groupNames + '")', time);
		return;
	}
	if ($jspx('.modal-open').lenght > 0) {

		setTimeout('refreshPanel(' + time + ',"' + eventName + '","'
				+ submitterId + '","' + ajaxSubmitterId + '","' + eventArgs
				+ '","' + groupNames + '")', time);
		return;
	}

	if (groupNames != null && groupNames != '') {
		var valid = validateListOfGroups(groupNames, submitterId);
		if (!valid) {
			setTimeout('refreshPanel(' + time + ',"' + eventName + '","'
					+ submitterId + '","' + ajaxSubmitterId + '","' + eventArgs
					+ '","' + groupNames + '")', time);
			return;
		}
	}
	setTimeout('postBack("' + ajaxSubmitterId + '","' + eventName + '","'
			+ submitterId + '","' + ajaxSubmitterId + '",false,"' + eventArgs
			+ '","","' + groupNames + '")', time);
}

function validateListOfGroups(groupNames, submitterId) {
	var valid = true;
	if (groupNames != null && groupNames != '') {
		var parts = groupNames.split(",");

		for (var i = 0; i < parts.length; i++) {
			var groupName = parts[i];
			try {
				var group = eval(groupName);
			} catch (e) {
				alert('Oops, it seems that you are using a validation group does not exist!');
				return false;
			}
			try {
				valid = valid
						&& validateFormData(eval(groupName), groupName,
								submitterId);
			} catch (e) {
			}
		}
	}
	if (!valid)
		resetButtons();
	return valid;
}

function resetButtons() {
	$jspx('[data-loading-text]').each(function() {
		$jspx(this).button('reset');
	});
}

function JSPX_INVOKE_SERVER_METHOD(methodName, invokerId, args, submitterId,
		ajaxSubmitterId) {
	postInvokeMethod(invokerId, methodName, args, submitterId, ajaxSubmitterId);
}