#!/bin/env bash
xterm -xrm 'XTerm.vt100.allowTitleOps: false' -T "Server A" -e 'echo "Server A started. Exit with [Strg]+[C]" && java -cp ./JTeeProxy.jar net.jteeproxy.testtools.EchoServer 12341 ' &
xterm -xrm 'XTerm.vt100.allowTitleOps: false' -T "Server B" -e 'echo "Server B started. Exit with [Strg]+[C]" && java -cp ./JTeeProxy.jar net.jteeproxy.testtools.EchoServer 12342 ' &
xterm -xrm 'XTerm.vt100.allowTitleOps: false' -T "JTeeProxy" -e 'echo "JTeeProxy started. Exit with [Strg]+[C]" && java -cp ./JTeeProxy.jar net.jteeproxy.JTeeProxy 12345 localhost 12341 localhost 12342 ' &
sleep 3
xterm -xrm 'XTerm.vt100.allowTitleOps: false' -T "Echo Client" -e 'echo "Client connected to JTeeProxy. Exit with [Strg]+[C]" && java -cp ./JTeeProxy.jar net.jteeproxy.testtools.EchoClient localhost 12345 ' &