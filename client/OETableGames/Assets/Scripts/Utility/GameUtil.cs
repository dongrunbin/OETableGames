using UnityEngine;

public static class GameUtil
{
    public static Vector3 GetRandomPos(Vector3 targetPos, float distance)
    {
        Vector3 v = new Vector3(0f, 0f, 1f);
        v = Quaternion.Euler(0f, Random.Range(0f, 360f), 0f) * v;

        Vector3 pos = v * distance * Random.Range(0.8f, 1f);
        return targetPos + pos;
    }

    public static Vector3 GetRandomPos(Vector3 currentPos, Vector3 targetPos, float distance)
    {
        Vector3 v = (currentPos - targetPos).normalized;
        v = Quaternion.Euler(0f, Random.Range(-90f, 90f), 0f) * v;

        Vector3 pos = v * distance * Random.Range(0.8f, 1f);
        return targetPos + pos;
    }

    public static void Vibrate()
    {
#if (UNITY_IPHONE || UNITY_ANDROID)
        UnityEngine.Handheld.Vibrate();
#endif
    }
}
