<?php /* Smarty version 3.1.27, created on 2017-09-05 21:10:37
         compiled from "/data/nginx/htdocs/api.wq02.wqmajiang.com/templates/main.html" */ ?>
<?php
/*%%SmartyHeaderCode:156588895159aea24d03ac28_76126096%%*/
if(!defined('SMARTY_DIR')) exit('no direct access allowed');
$_valid = $_smarty_tpl->decodeProperties(array (
  'file_dependency' => 
  array (
    '4b15916105a7eec6af65b42913cf65b0353375e4' => 
    array (
      0 => '/data/nginx/htdocs/api.wq02.wqmajiang.com/templates/main.html',
      1 => 1504594112,
      2 => 'file',
    ),
  ),
  'nocache_hash' => '156588895159aea24d03ac28_76126096',
  'variables' => 
  array (
    'game' => 0,
    'item' => 0,
    'passport' => 0,
    'agent' => 0,
    'weixin' => 0,
    'pay' => 0,
    'apple' => 0,
  ),
  'has_nocache_code' => false,
  'version' => '3.1.27',
  'unifunc' => 'content_59aea24d071796_16567570',
),false);
/*/%%SmartyHeaderCode%%*/
if ($_valid && !is_callable('content_59aea24d071796_16567570')) {
function content_59aea24d071796_16567570 ($_smarty_tpl) {

$_smarty_tpl->properties['nocache_hash'] = '156588895159aea24d03ac28_76126096';
?>


<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>oegame wiki</title>
<!-- 新 Bootstrap 核心 CSS 文件 -->
<link rel="stylesheet"
	href="http://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css">

<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<?php echo '<script'; ?>
 src="http://cdn.bootcss.com/jquery/1.11.3/jquery.min.js"><?php echo '</script'; ?>
>

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<?php echo '<script'; ?>
 src="http://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"><?php echo '</script'; ?>
>

<?php echo '<script'; ?>
 src="http://wiki.oegame.com/js/jquery.pin.js"><?php echo '</script'; ?>
>

</head>
<body>

	<div style="padding: 0px 20px;">

		<h2>Game</h2>
		<ol class="breadcrumb">
			<?php
$_from = $_smarty_tpl->tpl_vars['game']->value;
if (!is_array($_from) && !is_object($_from)) {
settype($_from, 'array');
}
$_smarty_tpl->tpl_vars['item'] = new Smarty_Variable;
$_smarty_tpl->tpl_vars['item']->_loop = false;
foreach ($_from as $_smarty_tpl->tpl_vars['item']->value) {
$_smarty_tpl->tpl_vars['item']->_loop = true;
$foreach_item_Sav = $_smarty_tpl->tpl_vars['item'];
?>
			<li><a href="javascript:build_game('<?php echo $_smarty_tpl->tpl_vars['item']->value['name'];?>
');">
					<?php echo $_smarty_tpl->tpl_vars['item']->value['name'];?>

			</a></li>
			<?php
$_smarty_tpl->tpl_vars['item'] = $foreach_item_Sav;
}
?>
		</ol>

		<h2>Passport</h2>
		<ol class="breadcrumb">
			<?php
$_from = $_smarty_tpl->tpl_vars['passport']->value;
if (!is_array($_from) && !is_object($_from)) {
settype($_from, 'array');
}
$_smarty_tpl->tpl_vars['item'] = new Smarty_Variable;
$_smarty_tpl->tpl_vars['item']->_loop = false;
foreach ($_from as $_smarty_tpl->tpl_vars['item']->value) {
$_smarty_tpl->tpl_vars['item']->_loop = true;
$foreach_item_Sav = $_smarty_tpl->tpl_vars['item'];
?>
			<li><a href="javascript:build_passport('<?php echo $_smarty_tpl->tpl_vars['item']->value['name'];?>
');">
					<?php echo $_smarty_tpl->tpl_vars['item']->value['name'];?>

			</a></li>
			<?php
$_smarty_tpl->tpl_vars['item'] = $foreach_item_Sav;
}
?>
		</ol>

		<h2>Agent</h2>
		<ol class="breadcrumb">
			<?php
$_from = $_smarty_tpl->tpl_vars['agent']->value;
if (!is_array($_from) && !is_object($_from)) {
settype($_from, 'array');
}
$_smarty_tpl->tpl_vars['item'] = new Smarty_Variable;
$_smarty_tpl->tpl_vars['item']->_loop = false;
foreach ($_from as $_smarty_tpl->tpl_vars['item']->value) {
$_smarty_tpl->tpl_vars['item']->_loop = true;
$foreach_item_Sav = $_smarty_tpl->tpl_vars['item'];
?>
			<li><a href="javascript:build_agent('<?php echo $_smarty_tpl->tpl_vars['item']->value['name'];?>
');">
					<?php echo $_smarty_tpl->tpl_vars['item']->value['name'];?>

			</a></li>
			<?php
$_smarty_tpl->tpl_vars['item'] = $foreach_item_Sav;
}
?>
		</ol>

		<h2>Weixin</h2>
		<ol class="breadcrumb">
			<?php
$_from = $_smarty_tpl->tpl_vars['weixin']->value;
if (!is_array($_from) && !is_object($_from)) {
settype($_from, 'array');
}
$_smarty_tpl->tpl_vars['item'] = new Smarty_Variable;
$_smarty_tpl->tpl_vars['item']->_loop = false;
foreach ($_from as $_smarty_tpl->tpl_vars['item']->value) {
$_smarty_tpl->tpl_vars['item']->_loop = true;
$foreach_item_Sav = $_smarty_tpl->tpl_vars['item'];
?>
			<li><a href="javascript:build_weixin('<?php echo $_smarty_tpl->tpl_vars['item']->value['name'];?>
');">
					<?php echo $_smarty_tpl->tpl_vars['item']->value['name'];?>

			</a></li>
			<?php
$_smarty_tpl->tpl_vars['item'] = $foreach_item_Sav;
}
?>
		</ol>

		<h2>Pay</h2>
		<ol class="breadcrumb">
			<?php
$_from = $_smarty_tpl->tpl_vars['pay']->value;
if (!is_array($_from) && !is_object($_from)) {
settype($_from, 'array');
}
$_smarty_tpl->tpl_vars['item'] = new Smarty_Variable;
$_smarty_tpl->tpl_vars['item']->_loop = false;
foreach ($_from as $_smarty_tpl->tpl_vars['item']->value) {
$_smarty_tpl->tpl_vars['item']->_loop = true;
$foreach_item_Sav = $_smarty_tpl->tpl_vars['item'];
?>
			<li><a href="javascript:build_pay('<?php echo $_smarty_tpl->tpl_vars['item']->value['name'];?>
');">
					<?php echo $_smarty_tpl->tpl_vars['item']->value['name'];?>

			</a></li>
			<?php
$_smarty_tpl->tpl_vars['item'] = $foreach_item_Sav;
}
?>
		</ol>

		<h2>Apple</h2>
		<ol class="breadcrumb">
			<?php
$_from = $_smarty_tpl->tpl_vars['apple']->value;
if (!is_array($_from) && !is_object($_from)) {
settype($_from, 'array');
}
$_smarty_tpl->tpl_vars['item'] = new Smarty_Variable;
$_smarty_tpl->tpl_vars['item']->_loop = false;
foreach ($_from as $_smarty_tpl->tpl_vars['item']->value) {
$_smarty_tpl->tpl_vars['item']->_loop = true;
$foreach_item_Sav = $_smarty_tpl->tpl_vars['item'];
?>
			<li><a href="javascript:build_apple('<?php echo $_smarty_tpl->tpl_vars['item']->value['name'];?>
');">
					<?php echo $_smarty_tpl->tpl_vars['item']->value['name'];?>

			</a></li>
			<?php
$_smarty_tpl->tpl_vars['item'] = $foreach_item_Sav;
}
?>
		</ol>
	</div>

	<div id="form-horizontal" style="display: none;">
		<form class="form-horizontal" action="/api/doPost/" method="post">
			<div id="form-body"></div>
			<div class="form-group">
				<div class="col-sm-offset-2 col-sm-10">
					<input class="btn btn-default" type="submit" value="submit" />
				</div>
			</div>
		</form>
	</div>

	<div id="res" style="padding: 30px;"></div>
	<?php echo '<script'; ?>
>
	function build(ctrl, action, args) {
		var html = "";
		for(var i=0; i<args.length; i++) {
			html += '<div class="form-group">';
			html += '<label class="col-sm-2 control-label">' + args[i][1];
			html += '</label>';
			html += '<div class="col-sm-6">';
			html += '<input type="text" class="form-control" name="' + args[i][1] + '"/>';
			html += '</div>';
			html += '</div>';
		}
		
		$("#res").html('');
		
		$("#form-body").html(html);
		$("#form-horizontal").show();
		
		$(".form-horizontal").unbind('submit');
		$(".form-horizontal").bind('submit', function(elem) {
			
			var key = [];
			var val = [];
			
			$.each($(".form-horizontal").find(":input"), function(index, elem) {
				if(elem.type == 'text') {
					key.push(elem.name);
					val.push(elem.value);
				}
			});
			
			
			var data = {
				'ctrl':ctrl,
				'url':action,
				'key':key,
				'val':val
			};
			
			$("#res").html('loading...');
			
			$.post('/api/doPost/', data, function(html) {
				$("#res").html(html);
			});
				
			return false;
		});
	}

		function build_game(name) {
			var args = new Array();
			<?php
$_from = $_smarty_tpl->tpl_vars['game']->value;
if (!is_array($_from) && !is_object($_from)) {
settype($_from, 'array');
}
$_smarty_tpl->tpl_vars['item'] = new Smarty_Variable;
$_smarty_tpl->tpl_vars['item']->_loop = false;
foreach ($_from as $_smarty_tpl->tpl_vars['item']->value) {
$_smarty_tpl->tpl_vars['item']->_loop = true;
$foreach_item_Sav = $_smarty_tpl->tpl_vars['item'];
?>
			args['<?php echo $_smarty_tpl->tpl_vars['item']->value['name'];?>
'] = <?php echo json_encode($_smarty_tpl->tpl_vars['item']->value['args']);?>
;
			<?php
$_smarty_tpl->tpl_vars['item'] = $foreach_item_Sav;
}
?>
			build('game', name, args[name]);
		}
		function build_passport(name) {
			var args = new Array();
			<?php
$_from = $_smarty_tpl->tpl_vars['passport']->value;
if (!is_array($_from) && !is_object($_from)) {
settype($_from, 'array');
}
$_smarty_tpl->tpl_vars['item'] = new Smarty_Variable;
$_smarty_tpl->tpl_vars['item']->_loop = false;
foreach ($_from as $_smarty_tpl->tpl_vars['item']->value) {
$_smarty_tpl->tpl_vars['item']->_loop = true;
$foreach_item_Sav = $_smarty_tpl->tpl_vars['item'];
?>
			args['<?php echo $_smarty_tpl->tpl_vars['item']->value['name'];?>
'] = <?php echo json_encode($_smarty_tpl->tpl_vars['item']->value['args']);?>
;
			<?php
$_smarty_tpl->tpl_vars['item'] = $foreach_item_Sav;
}
?>
			build('passport', name, args[name]);
		}
		function build_agent(name) {
			var args = new Array();
			<?php
$_from = $_smarty_tpl->tpl_vars['agent']->value;
if (!is_array($_from) && !is_object($_from)) {
settype($_from, 'array');
}
$_smarty_tpl->tpl_vars['item'] = new Smarty_Variable;
$_smarty_tpl->tpl_vars['item']->_loop = false;
foreach ($_from as $_smarty_tpl->tpl_vars['item']->value) {
$_smarty_tpl->tpl_vars['item']->_loop = true;
$foreach_item_Sav = $_smarty_tpl->tpl_vars['item'];
?>
			args['<?php echo $_smarty_tpl->tpl_vars['item']->value['name'];?>
'] = <?php echo json_encode($_smarty_tpl->tpl_vars['item']->value['args']);?>
;
			<?php
$_smarty_tpl->tpl_vars['item'] = $foreach_item_Sav;
}
?>
			build('agent', name, args[name]);
		}
		function build_apple(name) {
			var args = new Array();
			<?php
$_from = $_smarty_tpl->tpl_vars['apple']->value;
if (!is_array($_from) && !is_object($_from)) {
settype($_from, 'array');
}
$_smarty_tpl->tpl_vars['item'] = new Smarty_Variable;
$_smarty_tpl->tpl_vars['item']->_loop = false;
foreach ($_from as $_smarty_tpl->tpl_vars['item']->value) {
$_smarty_tpl->tpl_vars['item']->_loop = true;
$foreach_item_Sav = $_smarty_tpl->tpl_vars['item'];
?>
			args['<?php echo $_smarty_tpl->tpl_vars['item']->value['name'];?>
'] = <?php echo json_encode($_smarty_tpl->tpl_vars['item']->value['args']);?>
;
			<?php
$_smarty_tpl->tpl_vars['item'] = $foreach_item_Sav;
}
?>
			build('apple', name, args[name]);
		}
		function build_weixin(name) {
			var args = new Array();
			<?php
$_from = $_smarty_tpl->tpl_vars['weixin']->value;
if (!is_array($_from) && !is_object($_from)) {
settype($_from, 'array');
}
$_smarty_tpl->tpl_vars['item'] = new Smarty_Variable;
$_smarty_tpl->tpl_vars['item']->_loop = false;
foreach ($_from as $_smarty_tpl->tpl_vars['item']->value) {
$_smarty_tpl->tpl_vars['item']->_loop = true;
$foreach_item_Sav = $_smarty_tpl->tpl_vars['item'];
?>
			args['<?php echo $_smarty_tpl->tpl_vars['item']->value['name'];?>
'] = <?php echo json_encode($_smarty_tpl->tpl_vars['item']->value['args']);?>
;
			<?php
$_smarty_tpl->tpl_vars['item'] = $foreach_item_Sav;
}
?>
			build('weixin', name, args[name]);
		}
		function build_pay(name) {
			var args = new Array();
			<?php
$_from = $_smarty_tpl->tpl_vars['pay']->value;
if (!is_array($_from) && !is_object($_from)) {
settype($_from, 'array');
}
$_smarty_tpl->tpl_vars['item'] = new Smarty_Variable;
$_smarty_tpl->tpl_vars['item']->_loop = false;
foreach ($_from as $_smarty_tpl->tpl_vars['item']->value) {
$_smarty_tpl->tpl_vars['item']->_loop = true;
$foreach_item_Sav = $_smarty_tpl->tpl_vars['item'];
?>
			args['<?php echo $_smarty_tpl->tpl_vars['item']->value['name'];?>
'] = <?php echo json_encode($_smarty_tpl->tpl_vars['item']->value['args']);?>
;
			<?php
$_smarty_tpl->tpl_vars['item'] = $foreach_item_Sav;
}
?>
			build('pay', name, args[name]);
		}
	<?php echo '</script'; ?>
><?php }
}
?>