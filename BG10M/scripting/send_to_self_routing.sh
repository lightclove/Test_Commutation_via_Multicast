#!/bin/sh
ip rule add pref 1000 lookup local
ip rule del pref 0

ip rule add pref 10 iif eth0 lookup local
ip rule add pref 11 iif eth1 lookup local

ip rule add pref 100 to 192.168.0.100 lookup 100
ip rule add pref 101 to 192.168.0.101 lookup 101

ip route flush table 100
ip route flush table 101

ip route add default dev eth0 table 101
ip route add default dev eth1 table 100

ip rule
ip route show table all