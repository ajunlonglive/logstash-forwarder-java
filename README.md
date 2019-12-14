# logstash-forwarder-java(tcp-input)

Forked from https://github.com/didfet/logstash-forwarder-java

Logstash Lumberjack input is not robust enough, this fork replaced Lumberjack protocol to plain TCP.

## Example

### Logstash pipelines

```
input {
    tcp {
        port => 5055
        codec => json_lines
        #ssl_cert => "/etc/logstash/server.crt"
        #ssl_certificate_authorities => "/etc/logstash/ca.crt"
        #ssl_enable => true
        #ssl_key => "/etc/logstash/server.key"
        #ssl_key_passphrase => "${TCP_KEY_PASS}"
        #ssl_verify => false
        tcp_keep_alive => true
    }
}

filter {
}

output {
   stdout{} 
}
```
### Run logstash-forwarder

Just run this command :

    java -jar logstash-forwarder-java-X.Y.Z.jar -config /path/to/config/file.json

### Run logstash-forwarder with embedded JRE

Go to [https://github.com/medcl/logstash-forwarder-java/releases/tag/TCP-Forwarder](https://github.com/medcl/logstash-forwarder-java/releases/tag/TCP-Forwarder)

1. Download `logstash-forwarder-java.zip`, unzip it

2. Download related JDK to `logstash-forwarder-java` folder, unzip it, `7z x jdk8u232-b09-jre-<OS>.7z`

3. Change `run.sh`, update JDK path

4. Update `config.json`, with your server and file path

5. Run `run.sh`

For more JRE, download from here:

- https://adoptopenjdk.net/releases.html?variant=openjdk8&jvmVariant=hotspot

### Configuration

For help run `java -jar logstash-forwarder-java-X.Y.Z.jar -help`:

  - help
  - the ssl ca parameter points to a java [keystore](https://github.com/didfet/logstash-forwarder-java/blob/master/HOWTO-KEYSTORE.md) containing the root certificate of the server, not a PEM file
  - comments are C-style comments
  - multiline support with attributes "pattern", "negate" (true/false) and "what" (previous/next) (version 0.2.5)
  - filtering support with attributes "pattern" and "negate" (true/false) (version 0.2.5)
  - config (but only for a file, not a directory)
  - quiet
  - idle-timeout (renamed idletimeout)
  - spool-size (renamed spoolsize)
  - tail
  - debug : turn on debug logging level
  - trace : turn on trace logging level
  - signaturelength : size of the block used to compute the checksum
  - logfile : send logs to this file instead of stdout
  - logfilesize : maximum size of each log file (default 10M)
  - logfilenumber : number of rotated log files (default 5)

