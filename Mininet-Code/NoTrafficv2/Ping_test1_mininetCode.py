from mininet.net import Mininet
from mininet.node import RemoteController, OVSSwitch
#from mininet.node import Controller, OVSKernelSwitch
from mininet.cli import CLI
from mininet.log import setLogLevel
from mininet.link import TCLink

from select import poll, POLLIN
from time import time
import re
import os
from mininet.log import info, error, debug, output

outputFileName = 'results/ping/ping_test1.csv'

def _parsePingFull( pingOutput ):
    "Parse ping output and return all data."
    # Check for downed link
    if 'connect: Network is unreachable' in pingOutput:
        return (1, 0)
    r = r'(\d+) packets transmitted, (\d+) received'
    m = re.search( r, pingOutput )
    if m is None:
        error( '*** Error: could not parse ping output: %s\n' %
               pingOutput )
        return (1, 0, 0, 0, 0, 0)
    sent, received = int( m.group( 1 ) ), int( m.group( 2 ) )
    r = r'rtt min/avg/max/mdev = '
    r += r'(\d+\.\d+)/(\d+\.\d+)/(\d+\.\d+)/(\d+\.\d+) ms'
    m = re.search( r, pingOutput )
    rttmin = float( m.group( 1 ) )
    rttavg = float( m.group( 2 ) )
    rttmax = float( m.group( 3 ) )
    rttdev = float( m.group( 4 ) )
    return sent, received, rttmin, rttavg, rttmax, rttdev

def chunks( l, n ):
    "Divide list l into chunks of size n - thanks Stackoverflow"
    return [ l[ i: i + n ] for i in range( 0, len( l ), n ) ]

def startpings( host, hostsDST ):
    output( '\n\n' )
    print ( '*** Host %s (%s) will be pinging ips: %s' %
            ( host.name, host, hostsDST ) )
    for dst in hostsDST:
        if dst.IP() != host.IP():
            result = host.cmd('ping -c 30 %s' % dst.IP())
            outputs = _parsePingFull( result )
            sent, received, rttmin, rttavg, rttmax, rttdev = outputs
            output( "*** Results: \n" )
            output( " %s->%s: %s/%s, " % (host, dst, sent, received ) )
            output( "rtt min/avg/max/mdev %0.3f/%0.3f/%0.3f/%0.3f ms\n" %
                    (rttmin, rttavg, rttmax, rttdev) )
            os.system('echo "%s,%s,%s,%s,%0.3f,%0.3f,%0.3f,%0.3f" >> %s' % (host,dst,sent,received,rttmin,rttavg,rttmax,rttdev,outputFileName))

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

    if(os.path.exists('results/ping/ping_test1.csv')):
        os.system('rm results/ping/ping_test1.csv')

    os.system('echo "source,destination,sent,received,rtt min,rtt avg,rtt max,rtt mdev" >> %s' % (outputFileName))
    # Start pings        
    for host in hosts:
        startpings( host, hosts ) 

    #print '*** Running CLI ***'
    #CLI( net )

    #print '*** Stopping network ***'
    net.stop()

if __name__ == '__main__':
    setLogLevel( 'info' )
    topology()
