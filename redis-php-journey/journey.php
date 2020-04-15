<?php
// libraries
require __DIR__ . '/vendor/autoload.php';

// debug mode
$debug = false;

$start_time = microtime(true); 

// constants
define("PREFIX_PLACEHOLDER", "__PREFIX__");
define("SESSION_PLACEHOLDER", "__SESSION_ID__");

$redis_host = getenv("REDIS_HOST");
$redis_port = getenv("REDIS_PORT");
echo "REDIS START <br> Connect to ".$redis_host.":".$redis_port."<br />";
// configuration
$file = getenv("JOURNEY_FILE");
$client = new Predis\Client([
    'scheme' => 'tcp',
    'host'   => $redis_host,
    'port'   => $redis_port,
    'persistent'=> 1,
]);

// connect time in seconds 
$connect_time = microtime(true); 

echo "Processing ".$file;

$prefix = generateRandomString(10);
$sessionId = generateRandomString(40);

$handle = fopen($file, "r");
if ($handle) {
    while (($line = fgets($handle)) !== false) {
        // placeholders
        $line = str_replace(PREFIX_PLACEHOLDER, $prefix, $line);
        $line = str_replace(SESSION_PLACEHOLDER, $sessionId, $line);

        if ($debug) {
            echo "$line<br>";
        }

        $cmds = explode("\" ", $line);
        foreach($cmds as $key => $cmd) {
            $cmds[$key] = trim(str_replace("\"", "", $cmd));
        }

        // execute using redis
        $response = $client->executeRaw($cmds);

        // print response
        if ($debug) {
            if (is_array($response)) {
                print_r($response);
                echo "<br>";
            } else {
                echo substr($response, 0, 100)."<br>";
            }
        }
    }
    fclose($handle);
} else {
    echo "ERROR OPENING FILE!";
}

// End clock time in seconds 
$end_time = microtime(true); 
  
// Calculate script execution time 
$connect_time = ($connect_time - $start_time) * 1000;
$execution_time = ($end_time - $start_time) * 1000;

// report
echo "<br/>Took $execution_time s, connect to redis $connect_time ms.";


function generateRandomString($length = 10) {
    $characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    $charactersLength = strlen($characters);
    $randomString = '';
    for ($i = 0; $i < $length; $i++) {
        $randomString .= $characters[rand(0, $charactersLength - 1)];
    }
    return $randomString;
}

?>