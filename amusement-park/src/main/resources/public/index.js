var links
var pages
var email
var isAdmin

function init() {
	$.ajax({
		url : "/links",
		success : function(data) {
			links = {}
			$.each(data, function(i, e) {
				links[e.rel] = e.href
			})
			getUserData()
			$("#logout").attr("action", links.logout)
		}
	})
	pages = {
		loginAndSignUp : "/pages/login-and-sign-up.html",
		amusementPark : "/pages/amusement-park.html",
		machine : "/pages/machine.html"
	}
}

function getUserData() {
	$.ajax({
		url : links.me,
		success : function(data) {
			initHeader(data)
			getAmusementParkPage()
		},
		error : function(data) {
			$.ajax({
				url : pages.loginAndSignUp,
				success : function(data) {
					$("#content").html(data)
					$("#content").show()
					$("#loginForm").submit(function(event){event.preventDefault();});
				}
			})
		}
	})
}

function initHeader(data) {
	email = data.email
	$("#email").html(email)
	
	$("#photo").attr("src", data.photo)

	isAdmin = data.authority === "ROLE_ADMIN"

	$("#spendingMoney").html(data.spendingMoney)
	
	$("#upload").attr("onclick", "uploadMoney('"+data._links.uploadMoney.href+"')")
	
	$("#header").show()
}

function uploadMoney(url) {
	var money = $("#money").val()
		$.ajax({
			url : url,
			method : "POST",
			contentType : "application/json",
			data : money,
			success : function() {
				var successId=(Math.random()+ "").replace("0.", "")
				$("#moneyUploadResult").html("<div id='"+successId+"' class='alert alert-success' role='alert'>Success</div>")
				setTimeout(function() {
					$("#"+successId).remove()
				}, 7000);
	
				var spendingMoney = $("#spendingMoney")
				spendingMoney
						.html(parseInt(spendingMoney.html()) + parseInt(money))
			},
			error : function(data) {		
				var errorId=(Math.random()+ "").replace("0.", "")
				$("#moneyUploadResult").html("<div id='"+errorId+"' class='alert alert-danger' role='alert'>Error: " + data.responseText+"</div>")
				setTimeout(function() {
					$("#"+errorId).remove()
				}, 7000);
			}
		})
}

function clearUploadMoneyPopup() {
	var moneyUploadResult = $("#moneyUploadResult")
	moneyUploadResult.removeClass()
	moneyUploadResult.html("")
	
	$("#money").val("")
 }


function getAmusementParkPage() {
	$.ajax({
		url : pages.amusementPark,
		success : function(data) {
			$("#content").html(data)
			$("#content").show()
		}
	})
}