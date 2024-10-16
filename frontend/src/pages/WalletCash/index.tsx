import React, { useContext, useEffect, useState } from 'react'
import LandingTemplate from '../../components/templates/LandingTemplate'
import NavBar from '../../components/organisms/NavBar'
import DashboardHeader from '../../components/organisms/DashboardHeader'
import { DashboardFooter } from '../../components/organisms/DashboardFooter'
import styled from '@emotion/styled'
import { Stack } from '@mui/material'
import axios from 'axios'
import { BackendUrl } from '../../utils/constants'
import CashWatchlist from '../../components/organisms/CashWatchList'
import TypographyComponent from '../../components/atoms/Typography'
import theme from '../../theme'
import WalletTable from '../../components/organisms/WalletTable'
import numbro from 'numbro'
import { UserContext } from '../../App'

interface StyledStackProps {
  gap?: React.CSSProperties['gap']
  px?: React.CSSProperties['paddingLeft']
  py?: React.CSSProperties['paddingTop']
  direction?: 'row' | 'row-reverse' | 'column' | 'column-reverse'
  width?: React.CSSProperties['width']
  height?: React.CSSProperties['height']
}
const StyledStack = styled(Stack)<StyledStackProps>`
  padding-left: ${({ px }) => px};
  padding-top: ${({ py }) => py};
  flex-direction: ${({ direction }) => direction};
  width: ${({ width }) => width};
  height: ${({ height }) => height};
  gap: ${({ gap }) => gap};
`
const WalletPageContent = () => {
  const { user } = useContext(UserContext)
  const [walletBalance, setWalletBalance] = useState(0)
  const [transactions, setTransactions] = useState<any>([])
  const [coinData, setCoinData] = useState([])
  const fetchWallet = async () => {
    await axios
      .get(BackendUrl + '/wallets', {
        params: {
          userId: user.user.id,
        },
        headers: {
          Authorization: `Bearer ${sessionStorage.getItem('token')}`,
        },
      })
      .then((response) => {
        const usdWallet = response.data.find((item) => item.coinId === 11)
        console.log(usdWallet)
        setWalletBalance(usdWallet.amount)
      })
      .catch((e) => {
        console.log(e)
      })
  }
  const fetchTransactions = async () => {
    await axios
      .get(BackendUrl + '/wallets/transactions', {
        params: {
          userId: parseInt(user.user.id),
        },
        headers: {
          Authorization: `Bearer ${sessionStorage.getItem('token')}`,
        },
      })
      .then((response) => {
        console.log(response.data)
        response.data.forEach(async (item: any) => {
          const coin = coinData.find((coin) => coin.id == item.coinId)
          const dateString = item.time
          const properDateFormat = new Date(dateString)
          const currencyAmount =
            '$ ' + numbro(item.amount).format({ thousandSeparated: true })
          const currencyValue =
            (item.action === 'sell' ? '-' : '+') +
            item.coinValue +
            ' ' +
            coin.acronym
          setTransactions((prevTransactions: any) => [
            ...prevTransactions,
            {
              id: item.id,
              status: item.action,
              transactionState: item.status,
              from: item.fromUser,
              date: properDateFormat,
              currencyName: coin.name,
              convertedAmount: currencyAmount,
              currencyValue: currencyValue,
            },
          ])
        })
      })
      .catch((e) => {
        console.log(e)
      })
  }

  const fetchCoins = () => {
    axios
      .get(`${BackendUrl}/cryptos`, {
        headers: {
          Authorization: `Bearer ${sessionStorage.getItem('token')}`,
        },
      })
      .then((res) => {
        setCoinData(res.data)
      })
      .catch((e) => {
        console.log(e)
      })
  }
  useEffect(() => {
    fetchCoins()
    fetchWallet()
  }, [])

  useEffect(() => {
    if (coinData.length > 0) fetchTransactions()
  }, [coinData])

  return (
    <StyledStack
      direction={'column'}
      bgcolor={theme.palette.primary[100]}
      p={'1.5vh 1vw'}
      gap={'2vh'}
    >
      <CashWatchlist />
      <StyledStack gap={'1vh'}>
        <TypographyComponent
          variant={'subtitle2'}
          style={{ color: theme.palette.gray[500] }}
        >
          Wallet
        </TypographyComponent>
        <StyledStack
          p={'1vw'}
          bgcolor={theme.palette.gray[50]}
          direction={'row'}
          justifyContent={'space-between'}
          borderRadius={theme.spacing(1)}
        >
          <TypographyComponent
            variant={'subtitle1'}
            style={{ color: theme.palette.text.highemp }}
          >
            Total balance
          </TypographyComponent>
          <TypographyComponent
            variant={'subtitle1'}
            style={{ color: theme.palette.text.highemp }}
          >
            $ {numbro(walletBalance).format({ thousandSeparated: true })}
          </TypographyComponent>
        </StyledStack>
        <StyledStack bgcolor={theme.palette.gray.white}>
          <WalletTable
            data={transactions.sort(
              (a, b) => Number(new Date(b.date)) - Number(new Date(a.date))
            )}
          />
        </StyledStack>
      </StyledStack>
    </StyledStack>
  )
}

const WalletCashPage = () => {
  const [isActive, setIsActive] = useState(false)

  return (
    <LandingTemplate
      sidebar={<NavBar isActive={isActive} setIsActive={setIsActive} />}
      header={
        <DashboardHeader
          headerContent={'Trade'}
          sellEnabled={false}
          buyEnabled={false}
        />
      }
      footer={<DashboardFooter />}
      content={<WalletPageContent />}
    />
  )
}

export default WalletCashPage
