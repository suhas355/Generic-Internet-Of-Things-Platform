#include <iostream>
#include <cstdio>
#include <vector>
#include <algorithm>
#include <vector>
#include <list>
#include <queue>
#include <stack>
#include <cstring>
#include <limits.h>
#include <math.h>
#include <map>
#define ll long long
using namespace std;
struct ship
{
    int dist;
    short int spnumber;
};

long double D[20][20];
int prev[20];

int mindistance(long long int* dist, bool *spSet)
{
    int m=INT_MAX;
    int min_index;
    for(int i=0; i<20; i++)
    {
        if(!spSet[i] && dist[i] <= m)
        {
            m=dist[i];
            min_index = i;
        }
    }
    return min_index;
}


void printPath(int dest)
{
    if(prev[dest]==-1)
        return;
    else
        printPath(prev[dest]);
    cout<<dest<<endl;

}

void dijkstra(long double D[20][20], int source, int dest)
{
    long long int dist[20];
    bool spSet[20];
    for(int i=0; i<20; i++)
    {
        dist[i]=LLONG_MAX;
        spSet[i]=false;
    }
    dist[source]=0;
    int u;
    prev[source]=-1;
    for(int i=0; i<20; i++)
    {
        u=mindistance(dist, spSet);
        spSet[u]=true;
        //cout<<u<<" "<<dist[u]<<endl;
        if(u==dest)
            break;
        if(dist[u] == LLONG_MAX)
            break;
        for(int v=0; v<20; v++)
        {
            if(!spSet[v] && D[u][v] && dist[u] != LLONG_MAX  && dist[u]+D[u][v] < dist[v])
            {
                dist[v]=dist[u]+D[u][v];
                prev[v]=u;
            }
            
        }
    }
    printPath(dest);
}

int main()
{
    map <int, std::pair<ll, ll> > m1;
    m1[1]=std::make_pair(0,3);    m1[2]=std::make_pair(0,1);    m1[3]=std::make_pair(0,2);
    m1[4]=std::make_pair(3,6);    m1[5]=std::make_pair(3,7);    m1[6]=std::make_pair(1,7);
    m1[7]=std::make_pair(1,8);    m1[8]=std::make_pair(1,4);    m1[9]=std::make_pair(2,4);
    m1[10]=std::make_pair(2,5);   m1[11]=std::make_pair(5,8);   m1[12]=std::make_pair(6,11);
    m1[13]=std::make_pair(6,9);   m1[14]=std::make_pair(7,10);  m1[15]=std::make_pair(8,10);
    m1[16]=std::make_pair(8,13);  m1[17]=std::make_pair(5,13);  m1[18]=std::make_pair(9,12);
    m1[19]=std::make_pair(10,12); m1[20]=std::make_pair(11,14); m1[21]=std::make_pair(12,17);
    m1[22]=std::make_pair(13,15); m1[23]=std::make_pair(13,18); m1[24]=std::make_pair(14,16);
    m1[25]=std::make_pair(14,17); m1[26]=std::make_pair(15,17); m1[27]=std::make_pair(15,18);
    m1[28]=std::make_pair(16,19); m1[29]=std::make_pair(17,19); m1[30]=std::make_pair(18,19);
    for(int i=1; i<=30; i++)
    {
        long double edge; scanf("%Lf", &edge);
        D[m1[i].first][m1[i].second]=edge;
        //cout<<m1[i].first<<" "<<m1[i].second<<endl;
    }
    int curVert, dest=19;
    scanf("%d", &curVert);
    for(int i=0; i<20; i++)
        prev[i]=-1;
    dijkstra(D, curVert, dest);
	return 0;
}
