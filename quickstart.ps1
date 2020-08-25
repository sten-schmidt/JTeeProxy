[string]$jarFile =  "$PSScriptRoot\JTeeProxy.jar";
if (![System.IO.File]::Exists($jarFile)) {
    Write-Error -Message "The file $jarFile does not exist. Please build project first." 
} else {
    Write-Host "Starting Server A in a new powershell window. Exit with [Strg]+[C]"
    start powershell { java -cp .\JTeeProxy.jar test.tools.EchoServer 6789 }

    Write-Host "Starting Server B in a new powershell window. Exit with [Strg]+[C]"
    start powershell { java -cp .\JTeeProxy.jar test.tools.EchoServer 6790 }

    Write-Host "Start the proxy in a new powershell window. Exit with [Strg]+[C]"
    start powershell { java -cp .\JTeeProxy.jar net.stenschmidt.jteeproxy.JTeeProxy 1234 localhost 6789 localhost 6790 }

    Write-Host "Connect the Client to the Proxy. Exit with [Strg]+[C]. Type some text and press [Enter]:"
    java -cp .\JTeeProxy.jar test.tools.EchoClient localhost 1234
}