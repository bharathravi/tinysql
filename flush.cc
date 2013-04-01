#include<stdlib.h>
#include<unistd.h>

int main(int argc, char *argv[]) {

  while (true) {
    system("echo 1 > /proc/sys/vm/drop_caches");
  }
  return 0;
}
