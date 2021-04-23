//===================================================
//Author      : DRB
//CreateTime  ：2021/4/6 4:34:52
//Description ：
//===================================================

using System.Collections.Generic;

public class MahjongHelper
{
    [System.Flags]
    public enum CardType
    {
        None = 0,
        Single = 1,
        SameDouble = 2,
        SameTriple = 4,
        StraightTriple = 16,
        StraightLackMiddle = 17,
        StraightLack37 = 18,
        straightLackDouble = 19,
        StraightDouble = 20,
        DoubleOther = 128,
        TripleOther = 256,
        ThirteenYao = 131072,
    }

    public class CardCombination
    {
        public CardCombination(CardType type, List<Mahjong> lackCardIds, List<Mahjong> currentCombination)
        {
            CardType = type;
            LackCardIds = lackCardIds;
            CurrentCombination = currentCombination;
        }
        public CardType CardType { get; set; }
        public List<Mahjong> LackCardIds;
        public List<Mahjong> CurrentCombination;
        public int LackCount
        {
            get
            {
                int count = 0;
                if (CardType == CardType.straightLackDouble)
                {
                    count = 2;
                }
                else if (LackCardIds != null && LackCardIds.Count > 0)
                {
                    count = 1;
                }
                return count;
            }
        }
    }

    public static bool CheckUniversal(Mahjong mahjong, List<Mahjong> universal)
    {
        if (universal == null) return false;
        if (mahjong == null) return false;
        for (int i = 0; i < universal.Count; ++i)
        {
            if (mahjong.color == universal[i].color && mahjong.number == universal[i].number)
            {
                return true;
            }
        }
        return false;
    }

    public static bool HasMahjong(Mahjong mahjong, List<Mahjong> lst)
    {
        if (mahjong == null) return false;
        if (lst == null) return false;
        for (int i = 0; i < lst.Count; ++i)
        {
            if (lst[i].color == mahjong.color && lst[i].number == mahjong.number)
            {
                return true;
            }
        }
        return false;
    }

    public static bool ContainMahjong(Mahjong mahjong, List<Mahjong> lst)
    {
        if (mahjong == null) return false;
        if (lst == null) return false;
        for (int i = 0; i < lst.Count; ++i)
        {
            if (lst[i].index == mahjong.index)
            {
                return true;
            }
        }
        return false;
    }

    public static int GetSameCount(Mahjong mahjong, List<Mahjong> lst)
    {
        if (lst == null) return 0;
        if (mahjong == null) return 0;
        int ret = 0;
        for (int i = 0; i < lst.Count; ++i)
        {
            if (lst[i].color == mahjong.color && lst[i].number == mahjong.number)
            {
                ++ret;
            }
        }
        return ret;
    }

    public static List<Mahjong> GetSameMahjong(Mahjong mahjong, List<Mahjong> lst)
    {

        if (lst == null) return null;
        if (mahjong == null) return null;
        List<Mahjong> ret = new List<Mahjong>();
        for (int i = 0; i < lst.Count; ++i)
        {
            if (lst[i].color == mahjong.color && lst[i].number == mahjong.number)
            {
                ret.Add(lst[i]);
            }
        }
        return ret;
    }

    public static Mahjong GetNextMahjong(Mahjong mahjong, bool isZiContinuity)
    {
        if (mahjong == null) return null;
        int color = mahjong.color;
        int size = mahjong.number + 1;
        if (color < 4)
        {
            if (size > 9)
            {
                size = 1;
            }
        }
        else if (color == 5)
        {
            if (size > 3)
            {
                if (isZiContinuity)
                {
                    color = 4;
                }
                size = 1;
            }
        }
        else if (color == 4)
        {
            if (size > 4)
            {
                if (isZiContinuity)
                {
                    color = 5;
                }
                size = 1;
            }
        }
        Mahjong ret = new Mahjong(color, size);
        return ret;
    }

    public static Mahjong GetPrevMahjong(Mahjong mahjong, bool isZiContinuity)
    {
        if (mahjong == null) return null;
        int color = mahjong.color;
        int size = mahjong.number - 1;
        if (color < 4)
        {
            if (size <= 0)
            {
                size = 9;
            }
        }
        else if (color == 5)
        {
            if (size <= 0)
            {
                if (isZiContinuity)
                {
                    color = 4;
                    size = 4;
                }
                else
                {
                    size = 3;
                }
            }
        }
        else if (color == 4)
        {
            if (size <= 0)
            {
                if (isZiContinuity)
                {
                    color = 5;
                    size = 3;
                }
                else
                {
                    size = 4;
                }
            }
        }
        Mahjong ret = new Mahjong(color, size);
        return ret;
    }

    public static bool CheckLiangXi(List<Mahjong> mahjong)
    {
        if (mahjong == null || mahjong.Count < 3 || mahjong.Count > 4) return false;

        List<Mahjong> lstZi = new List<Mahjong>();
        List<Mahjong> lst1 = new List<Mahjong>();
        List<Mahjong> lst9 = new List<Mahjong>();

        for (int i = 0; i < mahjong.Count; ++i)
        {
            if (mahjong[i].color == 5)
            {
                lstZi.Add(mahjong[i]);
            }
            else if (mahjong[i].color < 4 && mahjong[i].number == 1)
            {
                if (HasMahjong(mahjong[i], lst1)) return false;
                lst1.Add(mahjong[i]);
            }
            else if (mahjong[i].color < 4 && mahjong[i].number == 9)
            {
                if (HasMahjong(mahjong[i], lst9)) return false;
                lst9.Add(mahjong[i]);
            }
            else
            {
                return false;
            }
        }

        if (lst1.Count > 0 && lst9.Count > 0) return false;

        if (lst1.Count > 0 && lst1.Count + lstZi.Count == 3) return true;
        if (lst9.Count > 0 && lst9.Count + lstZi.Count == 3) return true;
        if (lstZi.Count == 3 && lst1.Count == 0 && lst9.Count == 0) return true;
        if (lstZi.Count == 4 && lst1.Count == 0 && lst9.Count == 0)
        {
            for (int i = 1; i < lstZi.Count; ++i)
            {
                if (lstZi[i].number != lstZi[i - 1].number)
                {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private static List<List<CardCombination>> Result = new List<List<CardCombination>>();

    public static List<List<CardCombination>> CheckTing(
        List<Mahjong> Cards, List<MahjongGroup> usedMahjong, List<Mahjong> universal)
    {
        if (Cards == null) return null;
        Cards = new List<Mahjong>(Cards);
        SimpleSort(Cards);

        Result.Clear();
        int universalCount = 0;
        if (universal != null)
        {
            for (int i = Cards.Count - 1; i >= 0; --i)
            {
                if (CheckUniversal(Cards[i], universal))
                {
                    ++universalCount;
                    Cards.RemoveAt(i);
                }
            }
        }

        List<CardCombination> prevCombination = new List<CardCombination>();

        Check13Yao(Cards, universalCount);
        if (Result != null && Result.Count > 0)
        {
            return Result;
        }

        CheckSevenDouble(Cards, universalCount);

        Check(Cards, prevCombination, 0, 0, 0, universalCount);
        return Result;
    }

    public static bool CheckSevenDouble(List<Mahjong> Cards, List<Mahjong> universal)
    {
        if (Cards == null) return false;
        if (Cards.Count % 2 == 0) return false;
        SimpleSort(Cards);

        Result.Clear();
        int universalCount = 0;

        if (universal != null)
        {
            for (int i = Cards.Count - 1; i >= 0; --i)
            {
                if (CheckUniversal(Cards[i], universal))
                {
                    ++universalCount;
                    Cards.RemoveAt(i);
                }
            }
        }

        CheckSevenDouble(Cards, universalCount);

        return Result.Count > 0;
    }

    private static void CheckSevenDouble(List<Mahjong> Cards, int UniversalCount)
    {
        if (Cards.Count + UniversalCount < 13) return;
        if (Cards.Count + UniversalCount % 2 == 0) return;
        List<CardCombination> prevCombination = new List<CardCombination>();
        List<Mahjong> overplus = new List<Mahjong>(Cards);
        List<Mahjong> currSingle = new List<Mahjong>();
        List<Mahjong> currSameDouble = new List<Mahjong>();

        for (int i = overplus.Count - 1; i >= 0; --i)
        {
            if (HasMahjong(Cards[i], currSingle) && GetSameCount(Cards[i], overplus) <= GetSameCount(Cards[i], currSingle)) continue;
            Mahjong mahjong = overplus[i];
            currSingle.Add(mahjong);
            overplus.Remove(mahjong);
        }

        for (int i = currSingle.Count - 1; i >= 0; --i)
        {
            for (int j = overplus.Count - 1; j >= 0; --j)
            {
                if (currSingle[i].color == overplus[j].color && currSingle[i].number == overplus[j].number)
                {
                    currSameDouble.Add(currSingle[i]);
                    currSameDouble.Add(overplus[j]);
                    overplus.Remove(overplus[j]);
                    currSingle.Remove(currSingle[i]);
                    break;
                }
            }
        }

        if (currSameDouble.Count / 2 + UniversalCount < 6) return;

        for (int i = 0; i < currSingle.Count; ++i)
        {
            List<Mahjong> lackMahjong = new List<Mahjong>() { new Mahjong(currSingle[i]) };
            List<Mahjong> currCombination = new List<Mahjong>() { new Mahjong(currSingle[i]) };
            CardCombination combination = new CardCombination(CardType.SameDouble, lackMahjong, currCombination);
            prevCombination.Add(combination);
        }
        for (int i = 0; i < currSameDouble.Count; i += 2)
        {
            List<Mahjong> currCombination = new List<Mahjong>() { new Mahjong(currSameDouble[i]), new Mahjong(currSameDouble[i + 1]) };
            CardCombination combination = new CardCombination(CardType.SameDouble, null, currCombination);
            prevCombination.Add(combination);
        }

        if (currSingle.Count < UniversalCount)
        {
            List<Mahjong> lackList = new List<Mahjong>();
            for (int i = 1; i < 6; ++i)
            {
                for (int j = 1; j < 10; ++j)
                {
                    if (i == 4 && j > 4) break;
                    if (i == 5 && j > 3) break;

                    lackList.Add(new Mahjong(0, i, j));
                }
            }
            CardType type = CardType.DoubleOther;
            if (UniversalCount - currSingle.Count == 2)
            {
                type = CardType.TripleOther;
            }
            CardCombination com = new CardCombination(type, lackList, new List<Mahjong>());
            prevCombination.Add(com);
        }

        Result.Add(prevCombination);
    }

    private static readonly List<Mahjong> m_ThirteenYaoNeedList = new List<Mahjong>()
    { new Mahjong(1, 1),
      new Mahjong(1, 9),
      new Mahjong(2, 1),
      new Mahjong(2, 9),
      new Mahjong(3, 1),
      new Mahjong(3, 9),
      new Mahjong(4, 1),
      new Mahjong(4, 2),
      new Mahjong(4, 3),
      new Mahjong(4, 4),
      new Mahjong(5, 1),
      new Mahjong(5, 2),
      new Mahjong(5, 3),
    };

    private static void Check13Yao(List<Mahjong> mahjongs, int universalCount)
    {
        if (mahjongs == null || mahjongs.Count + universalCount != 13) return;
        bool hasSame = false;
        List<Mahjong> currCombination = new List<Mahjong>();
        for (int i = 0; i < mahjongs.Count; ++i)
        {
            if (HasMahjong(mahjongs[i], currCombination))
            {
                if (hasSame) return;
                hasSame = true;
            }
            if (!HasMahjong(mahjongs[i], m_ThirteenYaoNeedList)) return;
            currCombination.Add(mahjongs[i]);
        }
        if (currCombination.Count != mahjongs.Count) return;

        List<Mahjong> lackMahjong = new List<Mahjong>();
        if (hasSame)
        {
            for (int i = 0; i < m_ThirteenYaoNeedList.Count; ++i)
            {
                if (!HasMahjong(m_ThirteenYaoNeedList[i], currCombination))
                {
                    lackMahjong.Add(m_ThirteenYaoNeedList[i]);
                }
            }
        }
        else
        {
            for (int i = 0; i < m_ThirteenYaoNeedList.Count; ++i)
            {
                lackMahjong.Add(m_ThirteenYaoNeedList[i]);
            }
        }

        List<CardCombination> prevCombination = new List<CardCombination>();
        CardCombination combination = new CardCombination(CardType.ThirteenYao, lackMahjong, currCombination);
        prevCombination.Add(combination);
        Result.Add(prevCombination);
    }

    private static void Check(List<Mahjong> Cards, List<CardCombination> prevCombination, int lackCount, int singleCount, int sameDoubleCount, int UniversalCount)
    {
        if (lackCount > UniversalCount + 1)
        {
            return;
        }

        if (Cards.Count == 0)
        {
            List<CardCombination> newPrevCombination = new List<CardCombination>();
            for (int i = 0; i < prevCombination.Count; ++i)
            {
                List<Mahjong> lackMahjongs = prevCombination[i].LackCardIds == null ? null : new List<Mahjong>(prevCombination[i].LackCardIds);
                List<Mahjong> currentMahjongs = prevCombination[i].CurrentCombination == null ? null : new List<Mahjong>(prevCombination[i].CurrentCombination);
                newPrevCombination.Add(new CardCombination(prevCombination[i].CardType, lackMahjongs, currentMahjongs));
            }
            if (lackCount < UniversalCount)
            {
                List<Mahjong> lackList = new List<Mahjong>();
                for (int i = 1; i < 6; ++i)
                {
                    for (int j = 1; j < 10; ++j)
                    {
                        if (i == 4 && j > 4) break;
                        if (i == 5 && j > 3) break;

                        lackList.Add(new Mahjong(0, i, j));
                    }
                }
                CardType type = CardType.DoubleOther;
                if (UniversalCount - lackCount == 2)
                {
                    type = CardType.TripleOther;
                }
                CardCombination com = new CardCombination(type, lackList, new List<Mahjong>());
                newPrevCombination.Add(com);
            }

            sameDoubleCount = 0;
            for (int i = 0; i < newPrevCombination.Count; ++i)
            {
                if (newPrevCombination[i].CardType == CardType.SameDouble) ++sameDoubleCount;
            }
            if (sameDoubleCount == 1)
            {
                for (int i = 0; i < newPrevCombination.Count; ++i)
                {
                    if (newPrevCombination[i].CardType == CardType.SameDouble && newPrevCombination[i].CurrentCombination != null && newPrevCombination[i].CurrentCombination.Count == 2)
                    {
                        newPrevCombination[i].LackCardIds = null;
                        break;
                    }
                }
            }
            Result.Add(newPrevCombination);
            return;
        }
        CalculateStraightTriple(Cards, prevCombination, lackCount, singleCount, sameDoubleCount, UniversalCount);
        CalculateSameTriple(Cards, prevCombination, lackCount, singleCount, sameDoubleCount, UniversalCount);
        CalculateSameDouble(Cards, prevCombination, lackCount, singleCount, sameDoubleCount, UniversalCount);
        CalculateStraightDouble(Cards, prevCombination, lackCount, singleCount, sameDoubleCount, UniversalCount);
        CalculateStraightLackMiddle(Cards, prevCombination, lackCount, singleCount, sameDoubleCount, UniversalCount);
        CalculateSingle(Cards, prevCombination, lackCount, singleCount, sameDoubleCount, UniversalCount);
    }

    private static void CalculateSingle(List<Mahjong> Cards, List<CardCombination> prevCombination, int lackCount, int singleCount, int sameDoubleCount, int UniversalCount)
    {
        {
            if (sameDoubleCount == 0)
            {
                List<Mahjong> overPlusCards = new List<Mahjong>(Cards);
                List<CardCombination> lstCombination = new List<CardCombination>(prevCombination);
                List<Mahjong> currentLackCard = new List<Mahjong>() { new Mahjong(overPlusCards[0].index, overPlusCards[0].color, overPlusCards[0].number) };
                List<Mahjong> currentCombination = new List<Mahjong>() { new Mahjong(overPlusCards[0].index, overPlusCards[0].color, overPlusCards[0].number) };
                CardCombination newConbination = new CardCombination(CardType.SameDouble, currentLackCard, currentCombination);
                overPlusCards.RemoveAt(0);
                lstCombination.Add(newConbination);
                Check(overPlusCards, lstCombination, lackCount + 1, singleCount + 1, sameDoubleCount + 1, UniversalCount);
            }
        }

        {
            List<Mahjong> overPlusCards = new List<Mahjong>(Cards);
            List<CardCombination> lstCombination = new List<CardCombination>(prevCombination);
            List<Mahjong> currentLackCard = new List<Mahjong>() { new Mahjong(overPlusCards[0].index, overPlusCards[0].color, overPlusCards[0].number) };
            currentLackCard.Add(new Mahjong(overPlusCards[0].index, overPlusCards[0].color, overPlusCards[0].number));
            List<Mahjong> currentCombination = new List<Mahjong>() { new Mahjong(overPlusCards[0].index, overPlusCards[0].color, overPlusCards[0].number) };
            CardCombination newConbination = new CardCombination(CardType.SameTriple, currentLackCard, currentCombination);
            overPlusCards.RemoveAt(0);
            lstCombination.Add(newConbination);
            Check(overPlusCards, lstCombination, lackCount + 2, singleCount + 1, sameDoubleCount, UniversalCount);
        }

        {
            if (Cards[0].color > 3) return;

            List<Mahjong> overPlusCards1 = new List<Mahjong>(Cards);
            List<CardCombination> lstCombination1 = new List<CardCombination>(prevCombination);
            List<Mahjong> currentLackCard1 = new List<Mahjong>();
            if (Cards[0].number > 2)
            {
                currentLackCard1.Add(new Mahjong(0, Cards[0].color, Cards[0].number - 2));
            }
            if (Cards[0].number > 1)
            {
                currentLackCard1.Add(new Mahjong(0, Cards[0].color, Cards[0].number - 1));
            }
            if (Cards[0].number < 9)
            {
                currentLackCard1.Add(new Mahjong(0, Cards[0].color, Cards[0].number + 1));
            }
            if (Cards[0].number < 8)
            {
                currentLackCard1.Add(new Mahjong(0, Cards[0].color, Cards[0].number + 2));
            }
            List<Mahjong> currentCombination1 = new List<Mahjong>() { new Mahjong(overPlusCards1[0].index, overPlusCards1[0].color, overPlusCards1[0].number) };
            CardCombination newConbination1 = new CardCombination(CardType.straightLackDouble, currentLackCard1, currentCombination1);
            overPlusCards1.RemoveAt(0);
            lstCombination1.Add(newConbination1);
            Check(overPlusCards1, lstCombination1, lackCount + 2, singleCount + 1, sameDoubleCount, UniversalCount);
        }
    }

    private static void CalculateSameDouble(List<Mahjong> Cards, List<CardCombination> prevCombination, int lackCount, int singleCount, int sameDoubleCount, int UniversalCount)
    {
        if (Cards.Count < 2) return;

        if (Cards[0].color != Cards[1].color) return;
        if (Cards[0].number != Cards[1].number) return;

        {
            if (sameDoubleCount == 0)
            {
                List<Mahjong> overPlusCards = new List<Mahjong>(Cards);
                List<CardCombination> lstCombination = new List<CardCombination>(prevCombination);
                List<Mahjong> currentCombination = new List<Mahjong>() { new Mahjong(overPlusCards[0].index, overPlusCards[0].color, overPlusCards[0].number), new Mahjong(overPlusCards[1].index, overPlusCards[1].color, overPlusCards[1].number) };
                CardCombination newConbination = new CardCombination(CardType.SameDouble, null, currentCombination);
                overPlusCards.RemoveAt(1);
                overPlusCards.RemoveAt(0);
                lstCombination.Add(newConbination);
                Check(overPlusCards, lstCombination, lackCount, singleCount, sameDoubleCount + 1, UniversalCount);
            }
        }

        {
            List<Mahjong> overPlusCards = new List<Mahjong>(Cards);
            List<CardCombination> lstCombination = new List<CardCombination>(prevCombination);
            List<Mahjong> currentLackCard = new List<Mahjong>() { };
            currentLackCard.Add(new Mahjong(overPlusCards[0].index, overPlusCards[0].color, overPlusCards[0].number));
            List<Mahjong> currentCombination = new List<Mahjong>() { new Mahjong(overPlusCards[0].index, overPlusCards[0].color, overPlusCards[0].number), new Mahjong(overPlusCards[1].index, overPlusCards[1].color, overPlusCards[1].number) };
            CardCombination newConbination = new CardCombination(CardType.SameTriple, currentLackCard, currentCombination);
            overPlusCards.RemoveAt(1);
            overPlusCards.RemoveAt(0);
            lstCombination.Add(newConbination);
            Check(overPlusCards, lstCombination, lackCount + 1, singleCount, sameDoubleCount, UniversalCount);
        }
    }

    private static void CalculateSameTriple(List<Mahjong> Cards, List<CardCombination> prevCombination, int lackCount, int singleCount, int sameDoubleCount, int UniversalCount)
    {
        if (Cards.Count < 3) return;

        if (Cards[0].color != Cards[1].color) return;
        if (Cards[1].color != Cards[2].color) return;
        if (Cards[0].number != Cards[1].number) return;
        if (Cards[1].number != Cards[2].number) return;


        List<Mahjong> overPlusCards = new List<Mahjong>(Cards);
        List<CardCombination> lstCombination = new List<CardCombination>(prevCombination);
        List<Mahjong> currentCombination = new List<Mahjong>() { new Mahjong(overPlusCards[0]), new Mahjong(overPlusCards[1]), new Mahjong(overPlusCards[2]) };
        CardCombination newConbination = new CardCombination(CardType.SameTriple, null, currentCombination);
        lstCombination.Add(newConbination);
        overPlusCards.RemoveAt(2);
        overPlusCards.RemoveAt(1);
        overPlusCards.RemoveAt(0);
        Check(overPlusCards, lstCombination, lackCount, singleCount, sameDoubleCount, UniversalCount);
    }

    private static void CalculateStraightTriple(List<Mahjong> Cards, List<CardCombination> prevCombination, int lackCount, int singleCount, int sameDoubleCount, int UniversalCount)
    {
        if (Cards.Count < 3) return;
        if (Cards[0].color > 3) return;

        bool isFindSecond = false;
        bool isFindThird = false;
        int secondIndex = 0;
        int thirdIndex = 0;
        for (int i = 1; i < Cards.Count; ++i)
        {
            if (!isFindSecond && Cards[i].color == Cards[0].color && Cards[i].number == Cards[0].number + 1)
            {
                isFindSecond = true;
                secondIndex = i;
            }
            else if (!isFindThird && Cards[i].color == Cards[0].color && Cards[i].number == Cards[0].number + 2)
            {
                isFindThird = true;
                thirdIndex = i;
            }
            if (isFindSecond && isFindThird)
            {
                List<Mahjong> overPlusCards = new List<Mahjong>(Cards);
                List<CardCombination> lstCombination = new List<CardCombination>(prevCombination);
                List<Mahjong> currentCombination = new List<Mahjong>() { new Mahjong(overPlusCards[0]), new Mahjong(overPlusCards[secondIndex]), new Mahjong(overPlusCards[thirdIndex]) };
                CardCombination newConbination = new CardCombination(CardType.StraightTriple, null, currentCombination);
                if (secondIndex > thirdIndex)
                {
                    overPlusCards.RemoveAt(secondIndex);
                    overPlusCards.RemoveAt(thirdIndex);
                }
                else
                {
                    overPlusCards.RemoveAt(thirdIndex);
                    overPlusCards.RemoveAt(secondIndex);
                }
                lstCombination.Add(newConbination);
                overPlusCards.RemoveAt(0);
                Check(overPlusCards, lstCombination, lackCount, singleCount, sameDoubleCount, UniversalCount);
                break;
            }
        }
    }

    private static void CalculateStraightDouble(List<Mahjong> Cards, List<CardCombination> prevCombination, int lackCount, int singleCount, int sameDoubleCount, int UniversalCount)
    {
        if (Cards.Count < 2) return;
        if (Cards[0].color > 3) return;

        bool isFind = false;
        int findIndex = 0;
        for (int i = 1; i < Cards.Count; ++i)
        {
            if (Cards[i].color == Cards[0].color && Cards[i].number == Cards[0].number + 1)
            {
                isFind = true;
                findIndex = i;
                break;
            }
        }

        if (isFind)
        {
            List<Mahjong> overPlusCards = new List<Mahjong>(Cards);
            List<CardCombination> lstCombination = new List<CardCombination>(prevCombination);
            List<Mahjong> currentLackCard = null;
            CardType currType = CardType.StraightDouble;
            if (Cards[findIndex].number == 9)
            {
                currentLackCard = new List<Mahjong>() { new Mahjong(0, overPlusCards[0].color, overPlusCards[0].number - 1) };
                currType = CardType.StraightLack37;
            }
            else if (Cards[0].number == 1)
            {
                currentLackCard = new List<Mahjong>() { new Mahjong(0, overPlusCards[findIndex].color, overPlusCards[findIndex].number + 1) };
                currType = CardType.StraightLack37;
                if (Cards[0].color == 5)
                {
                    currType = CardType.StraightDouble;
                }
            }
            else
            {
                if (Cards[0].color == 5 && Cards[findIndex].number == 3)
                {
                    currentLackCard = new List<Mahjong>() { new Mahjong(0, overPlusCards[0].color, overPlusCards[0].number - 1) };
                }
                else
                {
                    currentLackCard = new List<Mahjong>() { new Mahjong(0, overPlusCards[0].color, overPlusCards[0].number - 1), new Mahjong(0, overPlusCards[findIndex].color, overPlusCards[findIndex].number + 1) };
                }

            }
            List<Mahjong> currentCombination = new List<Mahjong>() { new Mahjong(overPlusCards[0]), new Mahjong(overPlusCards[findIndex]) };
            CardCombination newConbination = new CardCombination(currType, currentLackCard, currentCombination);
            lstCombination.Add(newConbination);
            overPlusCards.RemoveAt(findIndex);
            overPlusCards.RemoveAt(0);
            Check(overPlusCards, lstCombination, lackCount + 1, singleCount, sameDoubleCount, UniversalCount);
        }
    }

    private static void CalculateStraightLackMiddle(List<Mahjong> Cards, List<CardCombination> prevCombination, int lackCount, int singleCount, int sameDoubleCount, int UniversalCount)
    {
        if (Cards.Count < 2) return;
        if (Cards[0].color > 3) return;

        bool isFind = false;
        int findIndex = 0;
        for (int i = 1; i < Cards.Count; ++i)
        {
            if (Cards[i].color == Cards[0].color && Cards[i].number == Cards[0].number + 2)
            {
                isFind = true;
                findIndex = i;
                break;
            }
        }
        if (isFind)
        {
            List<Mahjong> overPlusCards = new List<Mahjong>(Cards);
            List<CardCombination> lstCombination = new List<CardCombination>(prevCombination);
            List<Mahjong> currentCombination = new List<Mahjong>() { new Mahjong(overPlusCards[0]), new Mahjong(overPlusCards[findIndex]) };
            List<Mahjong> currentLackCard = new List<Mahjong>() { new Mahjong(0, overPlusCards[0].color, overPlusCards[0].number + 1) };
            CardCombination newConbination = new CardCombination(CardType.StraightLackMiddle, currentLackCard, currentCombination);
            lstCombination.Add(newConbination);
            overPlusCards.RemoveAt(findIndex);
            overPlusCards.RemoveAt(0);
            Check(overPlusCards, lstCombination, lackCount + 1, singleCount, sameDoubleCount, UniversalCount);
        }
    }

    private static List<Mahjong> m_UniversalList = new List<Mahjong>();
    private static List<Mahjong> m_Combination = new List<Mahjong>();

    public static void SimpleSort(List<Mahjong> lst)
    {
        lst.Sort((Mahjong card1, Mahjong card2) =>
        {
            if (card1.color < card2.color)
            {
                return -1;
            }
            else if (card1.color == card2.color)
            {
                if (card1.number < card2.number)
                {
                    return -1;
                }
                else if (card1.number == card2.number)
                {
                    return 0;
                }
                else
                {
                    return 1;
                }

            }
            else
            {
                return 1;
            }
        });
    }

    private static void UniversalLeftSort(List<Mahjong> lst, List<Mahjong> universal)
    {
        SimpleSort(lst);

        if (universal != null)
        {
            for (int i = 0; i < lst.Count; ++i)
            {
                if (CheckUniversal(lst[i], universal))
                {
                    Mahjong mahjong = lst[i];
                    lst.RemoveAt(i);
                    lst.Insert(0, mahjong);
                }
            }
        }
    }

    private static void UniversalBestSort(List<Mahjong> lst, List<Mahjong> universal)
    {
        if (lst == null || lst.Count == 0) return;
        if (lst[0].number == 0) return;
        m_UniversalList.Clear();
        m_Combination.Clear();
        if (universal != null)
        {
            for (int i = lst.Count - 1; i >= 0; --i)
            {
                if (CheckUniversal(lst[i], universal))
                {
                    m_UniversalList.Add(lst[i]);
                    lst.RemoveAt(i);
                    break;
                }
            }
        }

        lst.Sort((Mahjong card1, Mahjong card2) =>
        {
            if (card1.color < card2.color)
            {
                return -1;
            }
            else if (card1.color == card2.color)
            {
                if (card1.number < card2.number)
                {
                    return -1;
                }
                else if (card1.number == card2.number)
                {
                    return 0;
                }
                else
                {
                    return 1;
                }

            }
            else
            {
                return 1;
            }
        });

        if (m_UniversalList.Count > 0)
        {
            EliminateStraightTriple(lst, m_Combination);
            EliminateSameTriple(lst, m_Combination);

            for (int i = 0; i < lst.Count - 1; ++i)
            {
                if (m_UniversalList.Count == 0) break;
                if (lst[i].color == lst[i + 1].color && lst[i].number == lst[i + 1].number - 2)
                {
                    lst.Insert(i + 1, m_UniversalList[0]);
                    m_UniversalList.RemoveAt(0);
                    i = i + 2;
                }
            }

            for (int i = 0; i < lst.Count - 1; ++i)
            {
                if (m_UniversalList.Count == 0) break;

                if (CheckUniversal(lst[i], universal))
                {
                    continue;
                }
                if (i + 2 < lst.Count && CheckUniversal(lst[i + 2], universal))
                {
                    continue;
                }

                if (lst[i].color == lst[i + 1].color && lst[i].number == lst[i + 1].number - 1)
                {
                    if (lst[i].number == 1)
                    {
                        lst.Insert(i + 2, m_UniversalList[0]);
                    }
                    else if (lst[i].number == 8)
                    {
                        lst.Insert(i, m_UniversalList[0]);
                    }
                    else
                    {
                        lst.Insert(i + 2, m_UniversalList[0]);
                    }
                    m_UniversalList.RemoveAt(0);
                    i = i + 2;
                }
            }

            for (int i = 0; i < lst.Count - 1; ++i)
            {
                if (m_UniversalList.Count == 0) break;

                if (CheckUniversal(lst[i], universal))
                {
                    continue;
                }

                int sameCount = 0;
                for (int j = 0; j < lst.Count; ++j)
                {
                    if (lst[i].color == lst[j].color && lst[i].number == lst[j].number)
                    {
                        ++sameCount;
                    }
                }
                if (sameCount == 2)
                {
                    lst.Insert(i + 2, m_UniversalList[0]);
                    m_UniversalList.RemoveAt(0);
                    i += 2;
                }
            }

            for (int i = 0; i < lst.Count; ++i)
            {
                if (m_UniversalList.Count == 0) break;
                if (CheckUniversal(lst[i], universal))
                {
                    continue;
                }

                if (i == lst.Count - 1)
                {
                    lst.Insert(i + 1, m_UniversalList[0]);
                    m_UniversalList.RemoveAt(0);
                    break;
                }

                if (CheckUniversal(lst[i + 1], universal))
                {
                    continue;
                }
                if (lst[i + 1].color == lst[i].color && lst[i + 1].number == lst[i].number)
                {
                    continue;
                }
                if (lst[i + 1].color == lst[i].color && lst[i + 1].number - 1 == lst[i].number)
                {
                    continue;
                }

                lst.Insert(i + 1, m_UniversalList[0]);
                m_UniversalList.RemoveAt(0);
                ++i;
            }

            for (int i = 0; i < m_Combination.Count; ++i)
            {
                bool isInsert = false;
                for (int j = 0; j < lst.Count; ++j)
                {
                    if (CheckUniversal(lst[j], universal))
                    {
                        continue;
                    }
                    if (m_Combination[i].color < lst[j].color || (m_Combination[i].color == lst[j].color && m_Combination[i].number <= lst[j].number))
                    {
                        lst.Insert(j, m_Combination[i]);
                        isInsert = true;
                        break;
                    }
                }
                if (!isInsert)
                {
                    lst.Add(m_Combination[i]);
                }
            }

            if (m_UniversalList.Count > 0)
            {
                for (int i = 0; i < m_UniversalList.Count; ++i)
                {
                    lst.Add(m_UniversalList[i]);
                }
            }
        }
    }

    private static void EliminateStraightTriple(List<Mahjong> Cards, List<Mahjong> combination)
    {
        for (int j = 0; j < Cards.Count; ++j)
        {
            bool isFindSecond = false;
            bool isFindThird = false;
            int secondIndex = 0;
            int thirdIndex = 0;
            for (int i = j + 1; i < Cards.Count; ++i)
            {
                if (!isFindSecond && Cards[i].color == Cards[j].color && Cards[i].number == Cards[j].number + 1)
                {
                    isFindSecond = true;
                    secondIndex = i;
                }
                else if (!isFindThird && Cards[i].color == Cards[j].color && Cards[i].number == Cards[j].number + 2)
                {
                    isFindThird = true;
                    thirdIndex = i;
                }
                if (isFindSecond && isFindThird)
                {
                    if (secondIndex > thirdIndex)
                    {
                        combination.Add(Cards[secondIndex]);
                        combination.Add(Cards[thirdIndex]);
                        Cards.RemoveAt(secondIndex);
                        Cards.RemoveAt(thirdIndex);
                    }
                    else
                    {
                        combination.Add(Cards[thirdIndex]);
                        combination.Add(Cards[secondIndex]);
                        Cards.RemoveAt(thirdIndex);
                        Cards.RemoveAt(secondIndex);
                    }
                    combination.Add(Cards[j]);
                    Cards.RemoveAt(j);
                    --j;
                    break;
                }
            }
        }

    }

    private static void EliminateSameTriple(List<Mahjong> Cards, List<Mahjong> combination)
    {
        for (int i = 0; i < Cards.Count - 2; ++i)
        {
            if (Cards[i].color != Cards[i + 1].color) continue;
            if (Cards[i + 1].color != Cards[i + 2].color) continue;
            if (Cards[i].number != Cards[i + 1].number) continue;
            if (Cards[i + 1].number != Cards[i + 2].number) continue;
            combination.Add(Cards[i + 2]);
            combination.Add(Cards[i + 1]);
            combination.Add(Cards[i]);
            Cards.RemoveAt(i + 2);
            Cards.RemoveAt(i + 1);
            Cards.RemoveAt(i + 0);
            --i;
        }
    }
}
