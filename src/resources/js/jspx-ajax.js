var xmlHttp;
loadAjaxClient();

function submitAjax(form, invokerId, ajaxSubmiiterId) {
	showloading(ajaxSubmiiterId);
	if (form.enctype == 'multipart/form-data')
		sendFrame(form, invokerId, ajaxSubmiiterId);
	else
		sendAjax(form, invokerId, ajaxSubmiiterId);
}

function sendFrame(form, invokerId, ajaxSubmiiterId) {
	var frame = $jspx('#' + ajaxSubmiiterId + "_uploadFrame");
	// if there is no frame then send the ajax normally
	if (frame.length) {
		form.target = frame.get(0).name;
		form.submit();

	} else
		sendAjax(form, invokerId, ajaxSubmiiterId);
}

function sendAjax(form, invokerId, ajaxSubmiiterId) {
	var invoker = $jspx('#' + invokerId);
	var ajaxSubmiiter = $jspx('#' + ajaxSubmiiterId);
	var postData = preparePostData(form, invoker.get(0));
	var actionUrl;
	if (ajaxSubmiiter.attr('action'))
		actionUrl = ajaxSubmiiter.attr('action');
	else if ($jspx(form).attr('action'))
		actionUrl = $jspx(form).attr('action');
	else
		actionUrl = document.location.href;
	xmlHttp.open("POST", actionUrl, true);
	// xmlHttp.channel.QueryInterface(Components.interfaces.nsIHttpChannel).redirectionLimit
	// = 0;
	var formCharset = getCaharset();
	var contentType = "application/x-www-form-urlencoded; charset="
			+ formCharset;

	xmlHttp.setRequestHeader("Content-Type", contentType);
	xmlHttp.setRequestHeader("charset", formCharset);
	xmlHttp.onreadystatechange = render;
	xmlHttp.form = form;
	xmlHttp.invoker = invoker.get(0);
	xmlHttp.send(postData);
}

function getCaharset() {
	var metas = document.getElementsByTagName('meta');
	for ( var i = 0; i < metas.length; i++) {
		if (metas[i].httpEquiv == 'Content-Type') {
			var content = metas[i].content;
			var index = content.indexOf('charset=');
			if (index > 0)
				return content.substring(index + 8);
		}
	}
	return "UTF-8";
}

function preparePostData(form, invoker) {
	var result = "&jspx=ajax";
	result += parseContent(form);

	if (typeof invoker != 'undefined' && invoker != null
			&& typeof invoker == 'input'
			&& invoker.type.toLowerCase() == "image") {
		if (invoker.name == null || invoker.name == ""
				|| typeof invoker.name == "undefined")
			result += "&x=1&y=1"; // .x and .y coordinates calculation is not
		// supported.
		else
			result += "&" + encodeURIComponent(invoker.name) + ".x=1&"
					+ encodeURIComponent(invoker.name) + ".y=1";
	}
	if (typeof invoker != 'undefined' && invoker != null
			&& invoker.appendToRequest != 'undefined') {
		result += "&"
				+ $jspx('#' + invoker.appendToRequest + ' :input').serialize();
	}
	return result;
}

function parseContent(form) {
	var result = '';
	for ( var i = 0; i < form.elements.length; i++) {
		var el = form.elements[i];
		if (el.tagName.toLowerCase() == "select") {
			for ( var j = 0; j < el.options.length; j++) {
				var op = el.options[j];
				if (op.selected)
					result += "&" + encodeURIComponent(el.name) + "="
							+ encodeURIComponent(op.value);
			}
		} else if (el.tagName.toLowerCase() == "textarea") {
			result += "&" + encodeURIComponent(el.name) + "="
					+ encodeURIComponent(el.value);
		} else if (el.tagName.toLowerCase() == "input") {
			if (el.type.toLowerCase() == "checkbox"
					|| el.type.toLowerCase() == "radio") {
				if (el.checked)
					result += "&" + encodeURIComponent(el.name) + "="
							+ encodeURIComponent(el.value);
			} else if (el.type.toLowerCase() == "submit") {
				if (el == invoker) // is "el" the submit button that fired
					// the form submit?
					result += "&" + encodeURIComponent(el.name) + "="
							+ encodeURIComponent(el.value);
			} else if (el.type.toLowerCase() != "button") {
				result += "&" + encodeURIComponent(el.name) + "="
						+ encodeURIComponent(el.value);
			}
		}
	}
	return result;
}

function render() {
	var myform = xmlHttp.form;
	// invoker
	myform.elements[0].value = '';
	// event name
	myform.elements[1].value = '';
	// event args
	myform.elements[2].value = '';
	// event type
	myform.elements[3].value = '';
	// Group
	myform.elements[4].value = '';
	// event target
	myform.elements[5].value = '';

	if (xmlHttp.readyState == 4) {
		if (xmlHttp.status == 302) {
			document.location.href = xmlHttp.getResponseHeader("location");
		} else if (xmlHttp.status == 200) {
			var pageStatus = xmlHttp.getResponseHeader("JSPX-Page-Status");
			if (pageStatus != null && pageStatus == 'DispatchedPage') {
				replaceDOM(xmlHttp.responseText);
//				initiateButtons();
				resetButtons();
				return;
			}
			if (pageStatus != null && pageStatus == 'RedirectPage') {
				document.location.href = xmlHttp
						.getResponseHeader("JSPX-Page-Location");
				return;
			}
			var viewState = xmlHttp
					.getResponseHeader("JSPX-AjaxPanel-ViewState");
			// add View State
			if (myform.elements[myform.elements.length - 1]) {
				myform.elements[myform.elements.length - 1].value = viewState;
			}
			var appAjaxRenderedScript = xmlHttp
					.getResponseHeader("APP-Ajax-RenderedScript");
			var ajaxPanel = xmlHttp.getResponseHeader("JSPX-AjaxPanel");
			if (ajaxPanel == null || ajaxPanel == '') {
				// document.location.href = document.location.href;
				// if the response contains html/head/body then replace dom.
				var responseTxt= xmlHttp.responseText.toString();
				if(responseTxt!='' && responseTxt.indexOf('<html')>-1 && responseTxt.indexOf('<body')>-1)
					replaceDOM(responseTxt);
				eval(appAjaxRenderedScript);
				resetButtons();
				return;
			}
			var ajaxPanelAutoRefresh = xmlHttp
					.getResponseHeader("JSPX-AjaxPanel-Auto-Refresh");
			var ajaxPanelRendered = xmlHttp
					.getResponseHeader("JSPX-AjaxPanel-Rendered");
			var ajaxPanelRefresh = xmlHttp
			.getResponseHeader("JSPX-AjaxPanel-Refresh");
			eval(appAjaxRenderedScript);
			var aP = $jspx('#' + ajaxPanel);
			if (aP.length) {
				aP.html(xmlHttp.responseText);
				// here we should re-eval the script inside the return.
				/*
				 * $jspx(this).find('script').each(function() { try {
				 * jQuery.globalEval($jspx(this).get(0).innerHTML); } catch (e) { }
				 * }); eval(ajaxPanelRefresh);
				 */
				eval(ajaxPanelAutoRefresh);
				eval(ajaxPanelRendered);
				eval(ajaxPanelRefresh);
				resetButtons();
//				initiateButtons();
			} else
				alert(ajaxPanel);
			try
			{
				JSPX_AJAX_ON_RENDER();
			}
			catch(e)
			{}
			// parseScript(xmlHttp.responseText);

		} else if (xmlHttp.status == 0) {
		} else {
			document.body.innerHTML = xmlHttp.responseText;
			document.body.removeAttribute('style');
		}
	}
}

function showloading(ajaxSubmiiterId) {
	var loading = $jspx('#' + ajaxSubmiiterId + "AjaxLoading");
	if (!loading.length)
		return;
	var panel = $jspx('#' + ajaxSubmiiterId);
	loading.style.display = 'block';
	loading.style.position = 'absolute';
	loading.style.width = panel.get(0).offsetWidth;
	loading.style.height = panel.get(0).offsetHeight;
	loading.zIndex = 200;
}

function loadAjaxClient() {
	try {
		// Firefox, Opera 8.0+, Safari
		xmlHttp = new XMLHttpRequest();
	} catch (e) { // Internet Explorer
		try {
			xmlHttp = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (e) {
			try {
				xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (e) {
				alert("Your browser does not support AJAX!");
				return false;
			}
		}
	}
}

function replaceDOM(responseText) {
	document.body.innerHTML = responseText;
	// return;
	// eval the scripts again.
	var els = document.getElementsByTagName('script');
	var l = els.length;
	for ( var t = 0; t < l; t++) {
		try {
			// alert(els[t].innerHTML);
			if (els[t].innerHTML && els[t].innerHTML != '')
				jQuery.globalEval(els[t].innerHTML);
			// eval(els[t].innerHTML);
		} catch (e) {
			// TODO: handle exception
			// alert(els[t].innerHTML);
		}
	}
}