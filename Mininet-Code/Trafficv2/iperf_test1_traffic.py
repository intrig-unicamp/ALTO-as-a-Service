from mininet.net import Mininet
from mininet.node import RemoteController, OVSSwitch
#from mininet.node import Controller, OVSKernelSwitch
from mininet.cli import CLI
from mininet.log import setLogLevel
from mininet.link import TCLink

from select import poll, POLLIN
from time import time,sleep
import re
import os
from mininet.log import info, error, debug, output
from mininet.util import quietRun
import random

outputFileName = 'results/iperf/iperf_test1.csv'
ITGRecv_path = './D-ITG-2.8.1-r1023/bin/ITGRecv'
ITGSend_path = './D-ITG-2.8.1-r1023/bin/ITGSend'
ITGSend_protocol = 'TCP'
ITGSend_pkt_size = 512###108576#100##
ITGSend_rate = 1000#250###10##1000#
ITGSend_duration = 400000
scriptFile = "script_file"

def _parseIperf( iperfOutput ):
        """Parse iperf output and return bandwidth.
           iperfOutput: string
           returns: result string"""
        r = r'([\d\.]+ \w+/sec)'
        m = re.findall( r, iperfOutput )
        if m:            
            return m[-1]
        else:
            # was: raise Exception(...)
            error( 'could not parse iperf output: ' + iperfOutput )
            return ''

def iperf( hosts=None, l4Type='TCP', udpBw='10M' ):
        """Run iperf between two hosts.
           hosts: list of hosts; if None, uses opposite hosts
           l4Type: string, one of [ TCP, UDP ]
           returns: results two-element array of server and client speeds"""
        if not quietRun( 'which telnet' ):
            error( 'Cannot find telnet in $PATH - required for iperf test' )
            return
        #if not hosts:
        #    hosts = [ self.hosts[ 0 ], self.hosts[ -1 ] ]
        else:
            assert len( hosts ) == 2
        client, server = hosts
        output( '*** Iperf: testing ' + l4Type + ' bandwidth between ' )
        output( "%s and %s\n" % ( client.name, server.name ) )
        server.cmd( 'killall -9 iperf' )
        iperfArgs = 'iperf '
        bwArgs = ''
        if l4Type == 'UDP':
            iperfArgs += '-u '
            bwArgs = '-b ' + udpBw + ' '
        elif l4Type != 'TCP':
            raise Exception( 'Unexpected l4 type: %s' % l4Type )
        server.sendCmd( iperfArgs + '-s -f k', printPid=True )
        servout = ''
        while server.lastPid is None:
            servout += server.monitor()
        if l4Type == 'TCP':
            while 'Connected' not in client.cmd(
                    'sh -c "echo A | telnet -e A %s 5001"' % server.IP()):
                output('waiting for iperf to start up...')
                sleep(.5)
        cliout = client.cmd( iperfArgs + '-t 20 -f k -c ' + server.IP() + ' ' +
                             bwArgs )
        debug( 'Client output: %s\n' % cliout )
        server.sendInt()
        servout += server.waitOutput()
        debug( 'Server output: %s\n' % servout )
        result = [ _parseIperf( cliout ),_parseIperf( servout ) ]
        if l4Type == 'UDP':
            result.insert( 0, udpBw )
        output( '*** Results: %s\n' % result )
        
        os.system('echo "%s,%s,%s,%s" >> %s' % (client.name,server.name,result[0].split(" ")[0],result[1].split(" ")[0],outputFileName))


def start_traffic(src, dsts):    
    hdst = []
    for dst in dsts:
        if dst.IP() != src.IP():
            hdst.append(dst)

    server = '%s &' % (ITGRecv_path)
    strDst = ""
    if(os.path.exists(scriptFile)):
            os.system('rm '+scriptFile)  
    for dst in hdst:
        dst.cmd(server)
        strDst = strDst + dst.name + " (" + dst.IP() + "), "             
        os.system('echo "-a %s -T %s -c %d -C %d -t %d" >> %s' % (dst.IP(), ITGSend_protocol,ITGSend_pkt_size,ITGSend_rate,
                                                 ITGSend_duration,scriptFile)) 
    sleep(5)

    client = '%s %s &' % (ITGSend_path, scriptFile)
    src.cmd(client)
    print 'Started D-ITG flow %s (%s) -> %s' %\
                  (src.name, src.IP(), strDst)

def startIperf( host, hostsDST ):
    output( '\n\n' )         
       
    for dst in hostsDST:                                         
        if dst.IP() != host.IP():
            t = []  
            for h in hostsDST:
                if h.name != host.name and h.name != dst.name:                    
                    t.append(h)

            for h in t:
                start_traffic(h,t)
                sleep(10)

                hostsAux = []
                hostsAux.append(host)
                hostsAux.append(dst)
                iperf(hosts = hostsAux )
            
                h.cmd('killall '+ ITGRecv_path) 
                for d in t:                    
                    if d.IP() != h.IP():            
                        d.cmd('killall '+ ITGSend_path)    

def topology():

    #net = Mininet( controller=Controller, link=TCLink, switch=OVSKernelSwitch )
    net = Mininet( controller=RemoteController, link=TCLink, switch=OVSSwitch )

    print '*** Creating 22 nodes ***'
    ASN52864 = net.addSwitch( 'ASN52864' )
    ASN7 = net.addSwitch( 'ASN7' )
    ASN200070 = net.addSwitch( 'ASN200070' )
    ASN26121 = net.addSwitch( 'ASN26121' )
    ASN11537 = net.addSwitch( 'ASN11537' )
    ASN1 = net.addSwitch( 'ASN1' )
    ASN2 = net.addSwitch( 'ASN2' )
    ASN3356 = net.addSwitch( 'ASN3356' )
    ASN27678 = net.addSwitch( 'ASN27678' )
    ASN4 = net.addSwitch( 'ASN4' )
    ASN6057 = net.addSwitch( 'ASN6057' )
    ASN5 = net.addSwitch( 'ASN5' )
    ASN1853 = net.addSwitch( 'ASN1853' )
    ASN18881 = net.addSwitch( 'ASN18881' )
    ASN6 = net.addSwitch( 'ASN6' )
    ASN8 = net.addSwitch( 'ASN8' )
    ASN27750 = net.addSwitch( 'ASN27750' )
    #ASN3943 = net.addSwitch( 'ASN3943' )
    ASN28135 = net.addSwitch( 'ASN28135' )
    ASN10 = net.addSwitch( 'ASN10' )
    ASN3 = net.addSwitch( 'ASN3' )
    ASN9 = net.addSwitch( 'ASN9' )
    
    h1 = net.addHost( 'h1' )
    h2 = net.addHost( 'h2' )
    h3 = net.addHost( 'h3' )   
    h4 = net.addHost( 'h4' )
    h5 = net.addHost( 'h5' )
    h6 = net.addHost( 'h6' )
    h7 = net.addHost( 'h7' )
    h8 = net.addHost( 'h8' )
    h9 = net.addHost( 'h9' )
    h10 = net.addHost( 'h10' )

    c1 = net.addController( 'c1' )

    print '*** Creating Links ***'
    #1
    net.addLink('ASN26121','ASN1', bw=1000, delay='5ms')
    net.addLink('ASN26121','ASN2', bw=1000, delay='5ms')
    net.addLink('ASN26121','ASN18881', bw=1000, delay='5ms') 
    net.addLink('ASN26121','ASN3', bw=1000, delay='5ms')
   
    #net.addLink('ASN18881','ASN3', bw=1000, delay='5ms')    

    #2
    net.addLink('ASN1','ASN27678', bw=500, delay='20ms')
    #net.addLink('ASN52864','ASN2', bw=500, delay='20ms')
    #net.addLink('ASN52864','ASN4', bw=500, delay='20ms')
    #net.addLink('ASN18881','ASN28135', bw=500, delay='20ms')
    net.addLink('ASN4','ASN18881', bw=500, delay='20ms')
    net.addLink('ASN5','ASN3', bw=500, delay='20ms')

    #3
    net.addLink('ASN6057','ASN5', bw=100, delay='20ms')
    net.addLink('ASN27678','ASN6', bw=100, delay='20ms')

    #4
    net.addLink('ASN7','ASN6057', bw=100, delay='20ms')
    net.addLink('ASN3356','ASN6', bw=100, delay='20ms')

    #5
    net.addLink('ASN7','ASN27750', bw=100, delay='20ms')
    net.addLink('ASN3356','ASN1853', bw=100, delay='20ms')

    #6
    net.addLink('ASN11537','ASN27750', bw=100, delay='20ms')  
    net.addLink('ASN1853','ASN8', bw=100, delay='20ms')

    #7
    net.addLink('ASN11537','ASN9', bw=100, delay='20ms')
    net.addLink('ASN200070','ASN8', bw=100, delay='20ms')

    #8
    net.addLink('ASN10','ASN9', bw=100, delay='20ms')

    #9
    #net.addLink('ASN3943','ASN10', bw=100, delay='20ms')

    #hosts
    net.addLink('ASN1','h1', bw=10, delay='25ms')
    net.addLink('ASN2','h2', bw=10, delay='25ms')
    net.addLink('ASN3','h3', bw=10, delay='25ms')    
    net.addLink('ASN4','h4', bw=10, delay='25ms')
    net.addLink('ASN5','h5', bw=10, delay='25ms')
    net.addLink('ASN6','h6', bw=10, delay='25ms')
    net.addLink('ASN7','h7', bw=10, delay='25ms')
    net.addLink('ASN8','h8', bw=10, delay='25ms')
    net.addLink('ASN9','h9', bw=10, delay='25ms')
    net.addLink('ASN10','h10', bw=10, delay='25ms')

    print '*** Starting network ***'
    net.build()
    c1.start()

    ASN52864.start( [c1] )
    ASN7.start( [c1] )
    ASN200070.start( [c1] )
    ASN26121.start( [c1] )
    ASN11537.start( [c1] )
    ASN1.start( [c1] )
    ASN2.start( [c1] )
    ASN3356.start( [c1] )
    ASN27678.start( [c1] )
    ASN4.start( [c1] )
    ASN6057.start( [c1] )
    ASN5.start( [c1] )
    ASN1853.start( [c1] )
    ASN18881.start( [c1] )
    ASN6.start( [c1] )
    ASN8.start( [c1] )
    ASN27750.start( [c1] )
    #ASN3943.start( [c1] )
    ASN28135.start( [c1] )
    ASN10.start( [c1] )
    ASN3.start( [c1] )
    ASN9.start( [c1] )    

    hosts = net.hosts

    net.pingAll()

    if(os.path.exists('results/iperf/iperf_test1.csv')):
        os.system('rm results/iperf/iperf_test1.csv')

    os.system('echo "source,destination,TCP bandwidth (SRC->DST),TCP bandwidth (DST->SRC)" >> %s' % (outputFileName))
        
    #host = net.get('h3')         
    #startIperf(host,hosts)
    for host in hosts:        
        startIperf( host, hosts )
    
    #print '*** Running CLI ***'
    #CLI( net )

    #print '*** Stopping network ***'
    net.stop()

if __name__ == '__main__':
    setLogLevel( 'info' )
    topology()
