set terminal postscript eps enhanced font "Helvetica,26"
set encoding iso_8859_1

set size 1.5, 1.5

set border linewidth 2

#set grid
set key box

set pointsize 3
set key left top
set xrange [1:100]
set yrange [-0.1:]
set xlabel "Number of PIDs" font ",24"
set ylabel "Response time (sec)" font ",24" offset 2
set tics font ",22"

#set datafile separator ","
set output "Time_NetCostMap2.eps"

plot "./Time_NetCostMap.csv" using 1:10 with line lt 5 lw 10 lc rgb "red" title "Cost Map-Extra time (Proact)", \
     "./Time_NetCostMap.csv" using 1:6 with line lt 3 lw 10 lc rgb "green" title "Cost Map-Extra time (React)"
