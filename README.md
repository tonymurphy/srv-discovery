Consul DNS 
===========

Trying to use Consul to do service discovery. Trying to get this to work on Debian default install, and I don't know how to do it

Consul DNS listens on port 8600 for queries (https://www.consul.io/docs/agent/dns.html)

However DNS listens on port 53 by default, so queries need to be forward to consul. I did this using IPTables - but I don't know if this is the right approach. It does work however, but I don't know what the side effects might be

    sudo apt-get install dnsutils
    sudo iptables -t nat -A PREROUTING -p udp -m udp --dport 53 -j REDIRECT --to-ports 8600
    sudo iptables -t nat -A PREROUTING -p tcp -m tcp --dport 53 -j REDIRECT --to-ports 8600
    sudo iptables -t nat -A OUTPUT -d localhost -p udp -m udp --dport 53 -j REDIRECT --to-ports 8600
    sudo iptables -t nat -A OUTPUT -d localhost -p tcp -m tcp --dport 53 -j REDIRECT --to-ports 8600
    dig @127.0.0.1 -p 8600 bapi.service.consul. SRV
    dig @127.0.0.1 -p 8600 bapi-stub-node.node.consul. ANY
    dig @127.0.0.1 -p 53 bapi.service.consul. SRV
    dig @127.0.0.1 -p 53 bapi-stub-node.node.consul. ANY
    
This application attempts to lookup DNS from a Java Application, but it doesn't work, I don't know why
