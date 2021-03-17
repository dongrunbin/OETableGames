<?php
namespace Common;

use Core\ITools;
use Core\Util;
use Common\Model;

class Tools implements ITools {
	
	/**
	 * 获得md5密码
	 * 
	 * @param string $passport
	 * @param string $password
	 */
	public static function md5pswd($passport, $password) {
		return md5(md5(strtolower($passport)) . WEB_MIX . $password);
	}
	
	/**
	 * 表单提交结果
	 * 
	 * */
	public static function return_json($content = "操作成功", $target = "", $method = "refresh", $url = "", $status = 1) {
		return array(
			"content" => $content,
			"target"  => $target,
			"method"  => $method,
			"url"     => $url,
			"status"  => $status,
		);
	}
	
	/**
	 * 修改信息
	 *
	 * */
	public static function editinfo($admin_id) {
		return array(
			'edit_time' => REQUEST_TIME,
			'edit_user' => $admin_id,
			'edit_ip'   => Util::getClientIP()
		);
	}
	
	/**
	 * 添加信息
	 * 
	 * */
	public static function addinfo($admin_id) {
		return array(
			'add_time' => REQUEST_TIME,
			'add_user' => $admin_id,
			'add_ip'   => Util::getClientIP()
		);
	}
	
	/**
	 * 快速发布字段信息
	 * 
	 * */
	public static function fields() {
		
		return array(
			
			"type_id"       => array("type"=>"types", "limit"=>"1-10", "value"=>"0", "label"=>"类型ID"),
		        			
			"title"      => array("type"=>"text", "limit"=>"1-255", "value"=>"", "label"=>"标题"),
			"title2"     => array("type"=>"text", "limit"=>"1-255", "value"=>"", "label"=>"副标题"),
			"titleColor" => array("type"=>"color", "limit"=>"1-8",  "value"=>"#000000", "label"=>"标题颜色"),
			
			"cover1"     => array("type"=>"file", "limit"=>"1-255", "value"=>"", "label"=>"大图"),
			"cover2"     => array("type"=>"file", "limit"=>"1-255", "value"=>"", "label"=>"中图"),
			"cover3"     => array("type"=>"file", "limit"=>"1-255", "value"=>"", "label"=>"小图"),
			
			"author"     => array("type"=>"text", "limit"=>"1-255", "value"=>"", "label"=>"作者"),
			"origin"     => array("type"=>"text", "limit"=>"1-255", "value"=>"", "label"=>"来源"),
			"url"        => array("type"=>"text", "limit"=>"1-255", "value"=>"", "label"=>"连接"),
			
			"keyword"    => array("type"=>"text", "limit"=>"1-255", "value"=>"", "label"=>"关键字"),
			
			"extend1"    => array("type"=>"text", "limit"=>"0-255", "value"=>"", "label"=>"留字段保1"),
			"extend2"    => array("type"=>"text", "limit"=>"0-255", "value"=>"", "label"=>"留字段保2"),
			"extend3"    => array("type"=>"text", "limit"=>"0-255", "value"=>"", "label"=>"留字段保3"),
			"extend4"    => array("type"=>"text", "limit"=>"0-255", "value"=>"", "label"=>"留字段保4"),
			"extend5"    => array("type"=>"text", "limit"=>"0-255", "value"=>"", "label"=>"留字段保5"),
			

		    "explain"    => array("type"=>"textarea", "limit"=>"1-65535", "value"=>"", "label"=>"说明"),
			"summary"    => array("type"=>"editor", "limit"=>"1-65535", "value"=>"", "label"=>"摘要"),
			"content"    => array("type"=>"editor", "limit"=>"1-65535", "value"=>"", "label"=>"正文"),

		    "likes"      => array("type"=>"number",   "limit"=>"1-255", "value"=>"", "label"=>"喜欢"),
		        
			"atlas"      => array("type"=>"atlas",  "limit"=>"1-255", "value"=>"", "label"=>"图集"),
					        
		    "recommend"  => array("type"=>"checkbox", "limit"=>array("1"=>"是否推荐"), "value"=>"", "label"=>"推荐"),
		        
			"tags"       => array("type"=>"tags", "limit"=>"1-255", "value"=>"", "label"=>"标签"),
				
			"status"     => array("type"=>"radio", "limit"=>array("1"=>"开启", "-1"=>"关闭"), "value"=>"1", "label"=>"状态"),
			"location"   => array("type"=>"number", "limit"=>"1-10", "value"=>"0", "label"=>"位置"),
				
		);
	}
	
	public static function types($parent_id = 0) {
		$type = Model::Types();
		
		$data = $type->select("where `status` >= 1 and `parent_id` = '{$parent_id}'", '`id`, `parent_id`, `title`, `tags`');
		
		return $data;
	}
	
	public static function parents($type, $id) {
	    $array = array();
		if($type == 'type') {
		    $type = Model::Types($id);
		    array_unshift($array, $type->id);
		    while ($type->parent_id) {
		        $type->read($type->parent_id);
		        array_unshift($array, $type->id);
		    }
		}
		return $array;
	}
	
	public static function tags($ids = '') {
		$tag = Model::Tags();
		
		$all = $tag->select('where `status` >= 1', '`id`, `parent_id`, `title`, `tags`');
		
		$ids = explode(',', $ids);
		array_pop($ids);
		array_shift($ids);
				
		$array = array();
		
		foreach($all as $v) {
			if($v['parent_id'] == 0) {
			    $array[$v['id']] = array('info'=>$v, 'data'=>array());
			}else{
			    $checked = '';
			    if(in_array($v['id'], $ids)) {
			        $checked = 'checked';
			    }
			    $array[$v['parent_id']]['data'][$v['id']] = array('info'=>$v, 'data'=>array(), 'checked'=>$checked);
			}
		}
		
		return $array;
	}
	
	/**
	 * 分页
	 * 
	 * @param object $data
	 * */
	public static function paging($data, $url, $args = false) {
	   
		$limit  = $data['limit'];
		$page   = $data['page'];
		$rcount = $data['rcount'];
		$pcount = $data['pcount'];
		$next   = $data['next'];
		$prev   = $data['prev'];
		
		$pagenum = 7;
		
		$begin = 1;
		$end = $pagenum;
		
		if($end >= $pcount) {
			$end = $pcount;
		}else{
			if($page > ceil($pagenum / 2)) {
				$begin = $page - floor($pagenum / 2);
				$end   = $page + floor($pagenum / 2);
			}
			if($page + floor($pagenum / 2) >= $pcount) {
				$begin = $pcount - $pagenum + 1;
				$end   = $pcount;
			}
		}
		
		if(empty($args)) $args = array();
		
		$firsturl = vsprintf($url, array_merge($args, array(1)));
		
		$prevurl = vsprintf($url, array_merge($args, array($prev)));
		$nexturl = vsprintf($url, array_merge($args, array($next)));
		$lasturl = vsprintf($url, array_merge($args, array($pcount)));
		
		$pagstr= array();
		$pagstr[] = '<ul class="pagination justify-content-center">';
		
		if($page == $prev) {
			$pagstr[] = '<li class="page-item active"><a class="page-link" href="#">&lt;</a></li>';
		}else{
		    $pagstr[] = '<li class="page-item"><a class="page-link" href="'.$firsturl.'" target="editContent">&lt;&lt;</a></li>';
		    $pagstr[] = '<li class="page-item"><a class="page-link" href="'.$prevurl.'" target="editContent">&lt;</a></li>';
		}
		
		for($i=$begin; $i<=$end; $i++) {
			if($i == $page) {
				$pagstr[] = '<li class="page-item active"><a class="page-link" href="#"><u>'.$i.'</u></a></li>';
			}else{
				$pageurl = vsprintf($url, array_merge($args, array($i)));
				$pagstr[] = '<li class="page-item"><a class="page-link" href="'.$pageurl.'" target="editContent">'.$i.'</a></li>';
			}
		}
		if($page == $next) {
			$pagstr[] = '<li class="page-item active"><a class="page-link" href="#">&gt;</a></li>';
		}else{
			$pagstr[] = '<li class="page-item"><a class="page-link" href="'.$nexturl.'" target="editContent">&gt;</a></li>';
			$pagstr[] = '<li class="page-item"><a class="page-link" href="'.$lasturl.'" target="editContent">&gt;&gt;</a></li>';
		}
		$pagstr[] = '</ul>';
		
		return implode($pagstr, "\n");
	}
	
}
?>