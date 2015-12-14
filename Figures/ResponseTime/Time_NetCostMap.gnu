set terminal postscript eps enhanced font "Helvetica,26"
set encoding iso_8859_1

set size 1.5, 1.5

set border linewidth 2

#set grid
set key box

set pointsize 3
set key left top
set xrange [1:100]
#set yrange [0:3]
set xlabel "Number of PIDs" font ",24"
set ylabel "Response time (sec)" font ",24" offset 2
set tics font ",22"

#set datafile separator ","
set output "Time_NetCostMap.eps"

plot "./Time_NetCostMap.csv" using 1:2 with line lt 1 lw 10 lc rgb "blue" title "Network Map", \
     "./Time_NetCostMap.csv" using 1:7 with line lt 5 lw 10 lc rgb "red" title "Cost Map (Proactive)", \
     "./Time_NetCostMap.csv" using 1:3 with line lt 3 lw 10 lc rgb "green" title "Cost Map (Reactive)"
