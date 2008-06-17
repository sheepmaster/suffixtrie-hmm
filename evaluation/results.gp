reset
# set terminal postscript eps color enhanced
set terminal pdf
set datafile separator "\t"

set output "states.pdf"
plot "results_english_200806171240.txt" using 1 with lines

set output "perpl.pdf"
plot "results_english_200806171240.txt" using 0:($2*1.44269504088896340737) with lines

set output "states_perpl.pdf"
plot "results_english_200806171240.txt" using 1:($2*1.44269504088896340737) with lines
