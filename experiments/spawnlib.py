from subprocess import Popen, PIPE
import time
import fcntl, os

PORT_BASE = 5000

def set_nonblocking(fd):
    flags = fcntl.fcntl(fd, fcntl.F_GETFL)
    flags = flags | os.O_NONBLOCK
    fcntl.fcntl(fd, fcntl.F_SETFL, flags)

def spawn(i, num_players=2):
	my_port = PORT_BASE + i

	if i == 0:
		# this is the host
		cmd = "java Main %d %d" % (my_port, num_players)
	else:
		# join the host
		host_port = PORT_BASE
		cmd = "java Main %d 127.0.0.1 %d" % (my_port, host_port)
	
	return Popen(cmd, shell=True, stdin = PIPE, stdout = PIPE, stderr = PIPE, bufsize = 1)

class Process:
	def __init__(self, i, num_players):
		self.p = spawn(i, num_players)

		set_nonblocking(self.p.stdout)
		set_nonblocking(self.p.stderr)

		self.read_until("your name")
		self.write("peer #%d\n" % i)

		if i == 0:
			self.read_until("hosting")
		else:
			self.read_until("joining")

	def read(self):
		time.sleep(0.2)
		result = ""
		while True:
			try:
				r = self.p.stdout.read(1)
				result += r
				if r == "":
					break
			except IOError:
				break
		
		return result
	
	def read_until(self, text):
		result = ""
		while True:
			if text in result:
				break

			try:
				r = self.p.stdout.read(1)
				result += r
				if r == "":
					break
			except IOError:
				time.sleep(0.1)

		return result

	
	def write(self, text):
		return self.p.stdin.write(text)


def run_with_n_players(num_players):
	os.system("pkill --signal SIGKILL -f 'java Main' 2>/dev/null ") # clean up old experiments..
	time.sleep(1) # wait for them to terminate

	players = []
	for i in range(num_players):
		p = Process(i, num_players=num_players)
		players.append(p)

	# let them all autoplay
	for p in players:
		# note, we cannot read_until "press e to bla" because sometimes
		# players are skipped due to skip cards!
		p.write("a\n")

	# let them finish
	done = False
	while not done:
		for p in players:
			result = p.read()
			print len(result)
			if "is the winner" in result:
				done = True

