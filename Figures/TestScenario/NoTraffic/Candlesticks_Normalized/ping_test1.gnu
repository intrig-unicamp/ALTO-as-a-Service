set terminal postscript eps enhanced font "Helvetica,26"
set encoding iso_8859_1

set key top left
set xzeroaxis

set xlabel "Hosts" font ",24"
set ylabel "Normalized Network Latency (%)" font ",24" offset 1
set tics font ",22"

set boxwidth 0.2 absolute
set xrange [ 0.5 : 3.5 ]
#set yrange [ -15 : 40] noreverse nowriteback
set xtics('h1' 1,'h2' 2, 'h3' 3, 'h4' 4, 'h5' 5, 'h6' 6, 'h7' 7, 'h8' 8, 'h9' 9, 'h10' 10)
set output "noTraffic_ping_test1.eps"
plot 'ping_test1.csv' using 1:3:2:6:5 with candlesticks fs solid 0.25 notitle lt 1 lw 3 lc rgb "red" whiskerbars, \
'' using 1:4:4:4:4 with candlesticks lt -1 lw 4 lc rgb "black" notitle, \
'' using 1:($4+10):4 with labels notitle font "Helvetica-Bold,18"



