<?php
namespace Sdk;

use OSS\Core\OssException;
use OSS\OssClient;

class Alioss {

    const OSS_ACCESS_ID = 'LTAIcn4NDiPLOTtC';

    const OSS_ACCESS_KEY = 'YiKYHipdHQhe6r55E6Ubc1gULHzTOd';

    const OSS_ENDPOINT = 'oss-cn-hangzhou.aliyuncs.com';

    private $ossClient;

    private $bucketName;

    public function __construct($bucketName) {

        try {
            $this->ossClient = new OssClient(self::OSS_ACCESS_ID, self::OSS_ACCESS_KEY, self::OSS_ENDPOINT);
        } catch ( OssException $e ) {
            printf(__FUNCTION__ . "creating OssClient instance: FAILED\n");
            printf($e->getMessage() . "\n");
        }
        $this->bucketName = $bucketName;
    }

    public function upload($path, $file) {

        $base = $path['base'] . $path['spath'];
        $name = $base . $path['name'];
        
        $this->ossClient->createObjectDir($this->bucketName, dirname($base));
        
        $options = array ();
        
        $options['Content-Type'] = $file['type'];
        
        $this->ossClient->uploadFile($this->bucketName, $name, $file['tmp_name'], $options);
        
        file_put_contents('/tmp/swoole.process.log', 'upload: ' . $file['tmp_name'] . "\n\n\n", FILE_APPEND);
        
        @unlink($file['tmp_name']);
    
    }

    public function copy($path, $type, $file) {

        $base = $path['base'] . $path['spath'];
        $name = $base . $path['name'];
        
        $this->ossClient->createObjectDir($this->bucketName, dirname($base));
        
        $options = array ();
        
        $options['Content-Type'] = $type;
        
        $this->ossClient->uploadFile($this->bucketName, $name, $file, $options);
        
        file_put_contents('/tmp/swoole.process.log', 'copy: ' . $file . "\n\n\n", FILE_APPEND);
    
    }

    public function delete($path) {

        $base = $path['base'] . $path['spath'];
        $name = $base . $path['name'];
        
        $this->ossClient->deleteObject($this->bucketName, $name);
        
        file_put_contents('/tmp/swoole.process.log', 'delete: ' . $name . "\n\n\n", FILE_APPEND);
    
    }

}

?>