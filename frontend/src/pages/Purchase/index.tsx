import { Box } from '@mui/material'
import axios from 'axios'
import { useState, useEffect, useContext } from 'react'
import TypographyComponent from '../../components/atoms/Typography'
import { AmountDetails } from '../../components/organisms/AmountDetails'
import ChooseCrypto from '../../components/organisms/ChooseCrypto'
import { DashboardFooter } from '../../components/organisms/DashboardFooter'
import DashboardHeader from '../../components/organisms/DashboardHeader'
import DepositTo from '../../components/molecules/DepositTo'
import NavBar from '../../components/organisms/NavBar'
import { OrderSummary } from '../../components/organisms/OrderSummary'
import PaymentSuccess from '../../components/organisms/PaymentSuccess'
import LandingTemplate from '../../components/templates/LandingTemplate'
import theme from '../../theme'
import { BackendUrl, OrderAction, apiBase } from '../../utils/constants'
import DeliveryFee from '../../components/molecules/DeliveryFee'
import { useLocation, useNavigate } from 'react-router'
import numbro from 'numbro'
import { UserContext } from '../../App'
interface Crypto {
  id: any
  acronym: any
  name: any
  price: any
  iconUrl: any
}

const PurchasePage = () => {
  const location = useLocation()
  const queryParams = new URLSearchParams(location.search)
  const coinId = queryParams.get('coinId')
  const { user } = useContext(UserContext)

  const [cryptos, setCryptos] = useState([])
  const [selectItem, setSelectItem] = useState<Crypto>({
    id: 0,
    acronym: '',
    name: '',
    price: 0,
    iconUrl: '',
  })
  useEffect(() => {
    fetchData()
  }, [])
  console.log(coinId)
  const fetchData = async () => {
    try {
      const response = await axios.get(`${apiBase}/cryptos`, {
        headers: {
          Authorization: `Bearer ${sessionStorage.getItem('token')}`,
        },
      })
      const modifiedData = response.data.map((crypto: any) => {
        const { id, acronym, name, currentPrice, iconUrl } = crypto
        const price = currentPrice
        if (id === parseInt(coinId)) {
          setSelectItem({ id, acronym, name, price, iconUrl })
        }
        return { id, acronym, name, price, iconUrl }
      })
      setCryptos(modifiedData)
    } catch (error) {
      console.error('Error fetching data:', error)
    }
  }
  const fetchUSDWallet = () => {
    axios
      .get(`${BackendUrl}/wallets`, {
        params: {
          userId: user.user.id,
        },
        headers: {
          Authorization: `Bearer ${sessionStorage.getItem('token')}`,
        },
      })
      .then((res) => {
        const usdWallet = res.data.find((wallet) => wallet.coinId === 11)
        console.log('usdWallet', usdWallet.amount)
        setWalletBalance(usdWallet.amount)
      })
  }

  const PaymentMethodCaptions = [
    {
      label1: 'PaymentMethod',
      label2: 'Visa credit ...8845',
      icon: '../assets/icons/Group 43.svg',
    },
    {
      label1: 'Delivery fees',
      label2: `0.001 ${selectItem.acronym}`,
      icon: '../assets/icons/Group 43.svg',
    },
    {
      label1: 'Deposit to',
      label2: `${selectItem.name} wallet`,
      icon: '../assets/icons/Group 43.svg',
    },
  ]
  const [captions, setCaptions] = useState(PaymentMethodCaptions)
  useEffect(() => {
    fetchUSDWallet()
  }, [])
  useEffect(() => {
    const updatedCaptions = [...PaymentMethodCaptions]
    setCaptions(updatedCaptions)
  }, [selectItem])
  const [isActive, setIsActive] = useState(false)

  const [parentSliderValue, setParentSliderValue] = useState('')
  const [currentValue, setCurrentValue] = useState(0)
  const [walletBalance, setWalletBalance] = useState(0)

  const handleParentSliderChange = (newValue: any) => {
    setParentSliderValue(newValue)
  }
  const [showPayment, setShowPayment] = useState(false)

  const handleClick = async () => {
    try {
      await axios.post(
        `${apiBase}/wallets/transactions`,
        {
          user_id: user.user.id,
          transaction_type: 'buy',
          coin_id: selectItem.id,
          coin_amount: parseFloat(parentSliderValue),
          transaction_value: currentValue,
        },
        {
          headers: {
            Authorization: `Bearer ${sessionStorage.getItem('token')}`,
          },
        }
      )
      setShowPayment(true)
    } catch (error) {
      alert('Error during transaction')
    }
  }

  const navigate = useNavigate()
  return (
    <>
      <LandingTemplate
        sidebar={<NavBar isActive={isActive} setIsActive={setIsActive} />}
        header={
          showPayment ? (
            <DashboardHeader
              headerContent={'Checkout'}
              sellEnabled={false}
              buyEnabled={false}
            />
          ) : (
            <DashboardHeader
              headerContent={'Checkout'}
              sellEnabled={false}
              buyEnabled={false}
              sellHidden
              buyHidden
            />
          )
        }
        footer={<DashboardFooter />}
        content={
          showPayment ? (
            <Box
              display="flex"
              sx={{
                width: '100%',
                height: '100%',
                backgroundColor: theme.palette.primary[100],
              }}
              justifyContent="center"
              alignItems="center"
            >
              <PaymentSuccess
                totalAmount={parentSliderValue}
                transactionType={'Purchase'}
                crypto={selectItem.acronym}
                handleCoinClick={() => navigate('/walletCash')}
              />
            </Box>
          ) : (
            <Box
              display="flex"
              justifyContent="space-between"
              sx={{ backgroundColor: theme.palette.primary[100] }}
            >
              <Box sx={{ padding: '20px' }}>
                <TypographyComponent
                  variant={'subtitle1'}
                  children={'Buy Crypto'}
                  style={{
                    color: theme.palette.text.highemp,
                    marginBottom: '8px',
                  }}
                />
                <Box mb={'16px'}>
                  <ChooseCrypto
                    items={cryptos}
                    setSelectItem={setSelectItem}
                    selectedCoin={parseInt(coinId)}
                  />
                </Box>
                <Box mb={'16px'}>
                  <DepositTo
                    type={'Payment method'}
                    iconSrc={'../assets/icons/rupee.svg'}
                    iconTitle={'USD Coin(Cash)'}
                    remainingBalance={`$${numbro(walletBalance).format({
                      thousandSeparated: true,
                    })}`}
                  />
                </Box>
                <Box mb={'16px'}>
                  <AmountDetails
                    action={'purchase'}
                    amount={walletBalance}
                    currency={`${selectItem.acronym}`}
                    cryptoValue={selectItem.price}
                    setPurchaseValue={handleParentSliderChange}
                    setCurrentValue={setCurrentValue}
                  />
                </Box>
                <Box mb={'16px'}>
                  <DeliveryFee />
                </Box>
              </Box>
              <OrderSummary
                type={OrderAction}
                currency={`${parentSliderValue} ${selectItem.acronym}`}
                currencyCode={`1${selectItem.acronym} `}
                currencyValue={` $${numbro(selectItem.price).format({
                  thousandSeparated: true,
                })}`}
                captions={captions}
                convertedValue={`$${currentValue}`}
                handleTransaction={handleClick}
              />
            </Box>
          )
        }
      />
    </>
  )
}
export default PurchasePage
