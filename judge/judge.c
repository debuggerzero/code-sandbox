#include <unistd.h>
#include <fcntl.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/user.h>
#include <sys/ptrace.h>
#include <sys/time.h>
#include <sys/resource.h>
#include <sys/wait.h>

#define TIME_LIMIT 5000
#define MEMORY_LIMIT 512 * 1024

#define AC 0
#define PE 1
#define TLE 2
#define MLE 3
#define WA 4
#define RE 5
#define OLE 6
#define CE 7
#define SE 8

struct result
{
    int status;
    int timeUsed;
    int memoryUsed;
};

/*
 set the process limit(cpu and memory)
*/
void setProcessLimit()
{
    struct rlimit rl;

    /* set the time_limit (second)*/
    rl.rlim_cur = TIME_LIMIT / 1000;
    rl.rlim_max = rl.rlim_cur + 1;
    setrlimit(RLIMIT_CPU, &rl);

    /* set the memory_limit (b)*/
    rl.rlim_cur = MEMORY_LIMIT * 1024;
    rl.rlim_max = rl.rlim_cur + 1024;
    setrlimit(RLIMIT_DATA, &rl);
}

/*
 run the user process
*/
void runCmd(char *args[], char *in, char *out)
{
    int new_stdin = open(in, O_RDWR | O_CREAT, 0644);
    int new_stdout = open(out, O_RDWR | O_CREAT, 0644);
    setProcessLimit();
    if (new_stdin != -1)
    {
        dup2(new_stdin, fileno(stdin));
        dup2(new_stdout, fileno(stdout));
        if (execvp(args[0], args) == -1)
        {
            printf("====== Failed to start the process! =====\n");
        }
        close(new_stdin);
        close(new_stdout);
    }
    else
    {
        printf("====== Failed to open file! =====\n");
    }
}

/*
 monitor the user process
*/
void monitor(pid_t pid, struct result *rest)
{
    int status;
    struct rusage ru;
    if (wait4(pid, &status, 0, &ru) == -1)
        printf("wait4 failure");
    rest->timeUsed = ru.ru_utime.tv_sec * 1000 + ru.ru_utime.tv_usec / 1000 + ru.ru_stime.tv_sec * 1000 + ru.ru_stime.tv_usec / 1000;
    rest->memoryUsed = ru.ru_maxrss;
    if (WIFSIGNALED(status))
    {
        switch (WTERMSIG(status))
        {
        case SIGSEGV:
            if (rest->memoryUsed > MEMORY_LIMIT)
                rest->status = MLE;
            else
                rest->status = RE;
            break;
        case SIGALRM:
        case SIGXCPU:
            rest->status = TLE;
            break;
        default:
            rest->status = RE;
            break;
        }
    }
    else
    {
        if (rest->timeUsed > TIME_LIMIT)
            rest->status = TLE;
        else if (rest->memoryUsed > MEMORY_LIMIT)
            rest->status = MLE;
        else
            rest->status = AC;
    }
}

int run(char *args[], char *in, char *out)
{
    pid_t pid = vfork();
    if (pid < 0)
        printf("error in fork!\n");
    else if (pid == 0)
    {
        runCmd(args, in, out);
    }
    else
    {
        struct result rest;
        monitor(pid, &rest);
        printf("{\"status\":%d,\"timeUsed\":%d,\"memoryUsed\":%d}", rest.status, rest.timeUsed, rest.memoryUsed);
    }
}

void split(char **arr, char *str, const char *del)
{
    char *s = NULL;
    s = strtok(str, del);
    while (s != NULL)
    {
        *arr++ = s;
        s = strtok(NULL, del);
    }
    *arr++ = NULL;
}

int main(int argc, char *argv[])
{
    char *cmd[20];
    split(cmd, argv[1], "@");
    run(cmd, argv[2], argv[3]);
    return 0;
}